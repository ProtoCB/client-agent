package com.protocb.clientagent.logger;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

import static com.protocb.clientagent.config.EnvironmentVariables.*;

@Component
public class Logger {

    private String experimentSession;

    private File sessionLog;

    private Writer bufferedWriter;

    private Bucket bucket;

    @PostConstruct
    public void postConstruct(){
        try {

            FileInputStream serviceAccount = new FileInputStream(SERVICE_ACCOUNT_FILE_PATH);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(STORAGE_BUCKET)
                    .build();

            FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);

            bucket = StorageClient.getInstance(firebaseApp).bucket();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void createExperimentSessionLog() {
        try {
            FileUtils.cleanDirectory(new File(LOG_DIRECTORY));
            sessionLog = new File(LOG_DIRECTORY + "/" + AGENT_URL + ".csv");
            sessionLog.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(sessionLog));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shipExperimentSessionLog() {
        try {
            bufferedWriter.flush();
            bucket.create(experimentSession + "/" + AGENT_URL + ".csv", Files.readAllBytes(Paths.get(sessionLog.getAbsolutePath())),"csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String eventId, String message) {
        try {
            bufferedWriter.append(eventId + "," + Instant.now().toEpochMilli() + "," + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logSchedulingEvent(String message) {
        log(SCHEDULING_EVENT_ID, message);
    }

    public void logErrorEvent(String message) {
        log(ERROR_EVENT_ID, message);
    }

    public void setExperimentSession(String experimentSession) {
        this.experimentSession = experimentSession;
    }
}
