package com.autoanalytics.finanzVisualisierung;

import com.autoanalytics.finanzVisualisierung.service.DataService;
import com.autoanalytics.finanzVisualisierung.service.FileStorageService;
import com.autoanalytics.finanzVisualisierung.storage.StorageProperties;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import java.nio.file.*;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class FinanzVisualisierungApplication implements CommandLineRunner {

	private final JobLauncher jobLauncher;
	private final ApplicationContext applicationContext;
	private FileStorageService fileStorageService;

	// Constructor
	public FinanzVisualisierungApplication(JobLauncher jobLauncher, ApplicationContext applicationContext, FileStorageService fileStorageService) {
		this.jobLauncher = jobLauncher;
		this.applicationContext = applicationContext;
		this.fileStorageService = fileStorageService;
    }


	public static void main(String[] args) throws Exception {
		SpringApplication.run(FinanzVisualisierungApplication.class, args);
	}


	public BatchStatus runBatch(String jobName) throws Exception {
		System.out.println("runBatch() aufgerufen !!!!!");

		Job job = (Job) applicationContext.getBean(jobName);

		JobParameters jobParameters = new JobParametersBuilder()
				.addString("JobID", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();

		JobExecution jobExecution = jobLauncher.run(job, jobParameters);

		BatchStatus batchStatus = jobExecution.getStatus();
		while (batchStatus.isRunning()) {
			System.out.println("Still running...");
			Thread.sleep(5000L);
		}
		return batchStatus;
	}





	@Override
	public void run(String... args) throws Exception {

		//fileStorageService.deleteAll();		// (vorsicht) löscht verzeichnis 'uploads' falls vorhanden
		fileStorageService.init();

		// einen WatchService aufrufen, um uploads zu beobachten...falls eine Datei hochgeladen wird, wird einen Batch-Job aufgerufen
		WatchService watchService = FileSystems.getDefault().newWatchService();
		// Directory/File to be watched
		Path path = Paths.get("uploads").toAbsolutePath();		// finanzVisualisierung/uploads
		path.register(
				watchService,
				StandardWatchEventKinds.ENTRY_CREATE	// bei upload einer datei
		);
		System.out.println("Verzeichnis 'uploads' wird auf hochgeladene Dateien überwacht");
		WatchKey key;
		while ((key = watchService.take()) != null) {

			for (WatchEvent<?> event : key.pollEvents()) {
				System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");

				if(event.context().toString().contains("unternehmensdaten")) {
					runBatch("readCSVFilesJob");
				}
				else if(event.context().toString().contains("finanzdaten")) {
                	runBatch("readCSVFilesJob_Finanzen");
				}
			}
			key.reset();
		}
	}




}






































