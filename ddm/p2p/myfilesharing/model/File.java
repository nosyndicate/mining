package ddm.p2p.myfilesharing.model;

import java.util.Date;

public class File {
	private String md5;
	private String filename;
	private String status;
	private String percentageLoaded;
	private String filesize;
	private Date downloadTime;
	private String connectstatus;
	private Date finishDate;
	private String filePath;
	public String getMD5() {
		return md5;
	}
	public void setMD5(String md5) {
		this.md5 = md5;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPercentageLoaded() {
		return percentageLoaded;
	}
	public void setPercentageLoaded(String percentageLoaded) {
		this.percentageLoaded = percentageLoaded;
	}
	public String getFilesize() {
		return filesize;
	}
	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}
	public Date getDownloadTime() {
		return downloadTime;
	}
	public void setDownloadTime(Date downloadTime) {
		this.downloadTime = downloadTime;
	}
	public String getConnectstatus() {
		return connectstatus;
	}
	public void setConnectstatus(String connectstatus) {
		this.connectstatus = connectstatus;
	}
	public Date getFinishDate() {
		return finishDate;
	}
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
}
