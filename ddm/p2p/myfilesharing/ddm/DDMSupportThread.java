package ddm.p2p.myfilesharing.ddm;

public class DDMSupportThread implements Runnable{

	private DDMService service;
	
	public DDMSupportThread(DDMService _service)
	{
		service = _service;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		updateSupports();
	}
	
	public void updateSupports(){
		
	}
}
