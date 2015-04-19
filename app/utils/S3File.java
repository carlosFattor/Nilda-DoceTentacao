package utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import play.Logger;
import plugins.S3Plugin;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;


public class S3File {
 
	private String bucket;
	private String name;
	private String path;
	private String url = "https://s3.amazonaws.com/";
	private File file;

	public S3File() {
		super();
	}

	public S3File setNameFile(String nameFile) {
		this.name = nameFile;
		return this;
	}

	public S3File setPath(String path) {
		this.path = path;
		return this;
	}
	
	public S3File setFile(File file){
		this.file = file;
		return this;
	}
	
	public String getPartiaPath(){
		return url+S3Plugin.s3Bucket+"/";
	}
	
	public URL getUrl() {
		try {
			return new URL(this.url + this.bucket + "/" + this.path	+ this.name);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Invalid URL");
		}
	}
	
	public String getOnlyFileName(){
		return name;
	}

	private String getActualFileName() {
		return path + name;
	}

	public S3File save() {
		if (S3Plugin.amazonS3 == null) {
			Logger.error("Could not save because amazonS3 was null");
			throw new RuntimeException("Could not save");
		} else {
			this.bucket = S3Plugin.s3Bucket;
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket,
					this.getActualFileName() , file);
			putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
			S3Plugin.amazonS3.putObject(putObjectRequest);
			Logger.info("Image successfully uploaded");
			return this;
		}
	}

	public S3File delete() {
		if (S3Plugin.amazonS3 == null) {
			Logger.error("Could not save because amazonS3 was null");
			throw new RuntimeException("Could not save");
		} else {
			this.bucket = S3Plugin.s3Bucket;
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket,
					getActualFileName(), file);
			putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
			S3Plugin.amazonS3.deleteObject(bucket, getActualFileName());
			Logger.info("Image successfully deleted");
			return this;
		}
	}
}
