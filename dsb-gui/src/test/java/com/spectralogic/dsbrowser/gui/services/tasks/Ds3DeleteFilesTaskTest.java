package com.spectralogic.dsbrowser.gui.services.tasks;

import com.google.common.collect.ImmutableList;
import com.spectralogic.dsbrowser.gui.components.ds3panel.ds3treetable.Ds3TreeTableValue;
import com.spectralogic.dsbrowser.gui.services.Workers;
import com.spectralogic.dsbrowser.gui.services.newSessionService.SessionModelService;
import com.spectralogic.dsbrowser.gui.services.savedSessionStore.SavedCredentials;
import com.spectralogic.dsbrowser.gui.services.savedSessionStore.SavedSession;
import com.spectralogic.dsbrowser.gui.services.sessionStore.Session;
import com.spectralogic.dsbrowser.gui.util.SessionConstants;
import com.spectralogic.dsbrowser.gui.util.StringConstants;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class Ds3DeleteFilesTaskTest {

    private final Workers workers = new Workers();
    private Session session;
    private boolean successFlag = false;

    @Before
    public void setUp() {
        new JFXPanel();
        Platform.runLater(() -> {
            final SavedSession savedSession = new SavedSession(SessionConstants.SESSION_NAME,
                    SessionConstants.SESSION_PATH, SessionConstants.PORT_NO, null,
                    new SavedCredentials(SessionConstants.ACCESS_ID, SessionConstants.SECRET_KEY),
                    false);
            session = new CreateConnectionTask().createConnection(
                    SessionModelService.setSessionModel(savedSession, false));
        });
    }

    @Test
    public void deleteFiles() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                final ImmutableList<String> buckets = new ImmutableList.Builder<String>()
                        .add(SessionConstants.DS3_PANEL_SERVICE_TEST_BUCKET_NAME)
                        .build();

                final Ds3TreeTableValue ds3TreeTableValue = new Ds3TreeTableValue(
                        SessionConstants.DS3_PANEL_SERVICE_TEST_BUCKET_NAME, SessionConstants.ALREADY_EXIST_FILES,
                        Ds3TreeTableValue.Type.File, 0L, StringConstants.EMPTY_STRING,
                        StringConstants.TWO_DASH, false, Mockito.mock(HBox.class));

                final TreeItem<Ds3TreeTableValue> value = new TreeItem<>();
                value.setValue(ds3TreeTableValue);

                final ImmutableList<TreeItem<Ds3TreeTableValue>> values =
                        new ImmutableList.Builder<TreeItem<Ds3TreeTableValue>>()
                                .add(value)
                                .build();

                final ArrayList<Ds3TreeTableValue> filesToDelete = new ArrayList<>(values
                        .stream()
                        .map(TreeItem::getValue)
                        .collect(Collectors.toList())
                );
                final Map<String, List<Ds3TreeTableValue>> bucketObjectsMap = filesToDelete.stream()
                        .collect(Collectors.groupingBy(Ds3TreeTableValue::getBucketName));

                final Ds3DeleteFilesTask deleteFilesTask = new Ds3DeleteFilesTask(session.getClient(),
                        buckets, bucketObjectsMap);
                workers.execute(deleteFilesTask);
                deleteFilesTask.setOnSucceeded(event -> {
                    successFlag = true;
                    latch.countDown();
                });
                deleteFilesTask.setOnFailed(event -> latch.countDown());
                deleteFilesTask.setOnCancelled(event -> latch.countDown());
            } catch (final Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });
        latch.await();
        assertTrue(successFlag);
    }
}