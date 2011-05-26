package ddm.p2p.myfilesharing.ddm;

public class DDMConfidenceThread implements Runnable{
	private DDMService service;
	
	public DDMConfidenceThread(DDMService _service)
	{
		service = _service;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		updateConfidences();
	}
	
	public void updateConfidences(){
		
	}
	
}
