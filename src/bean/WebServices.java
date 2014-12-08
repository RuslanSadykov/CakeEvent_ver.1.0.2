package bean;

/**
 * Created by Phaenir on 07.12.2014.
 */
public enum WebServices {
	LOCALHOST   ("http://127.0.0.1:8080"),      WILDAU          ("http://194.95.44.187:8008"),
	TFH_WILDAU  ("http://www.tfh-wildau.de/"),  WILDAU_DEBUG    ("http://194.95.44.187:8009");

	private String string;
	private int port;
	private String web;
	WebServices(String str){
		setWeb(str);
		setString(str);
	}
	WebServices(String web, int port){
		setWeb(web);
		setPort(port);
		setString(web,port);
	}
	public String getWebService(){
		return this.string;
	}
	public void setWeb(String str){
		this.web=str;
	}
	public void setPort(int port){
		this.port=port;
	}
	private void setString(String web, int port){
		this.string=web+":"+port;
	}
	private void setString(String string){
		this.string=string;
	}
}