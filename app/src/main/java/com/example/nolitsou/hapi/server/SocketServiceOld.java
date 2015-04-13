package com.example.nolitsou.hapi.server;

public class SocketServiceOld {
    /*
    private final IBinder mBinder = new SocketBinder();
	private String url;
	private static ServerLink serverLink;
	public static boolean piConnected = false;
	@Override
	public void onCreate() {        
		super.onCreate();
	}

	@Override
	public void onDestroy() {   
		ServerLink.getSocket().disconnect();
		ServerLink.getSocket().close();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		this.setUrl(Settings.host);
		ServerLinkTask task = new ServerLinkTask(this);
		task.execute(getUrl());
		return Service.START_STICKY;
	}
	@Override
	public IBinder onBind(Intent arg0) {        
		return mBinder;
	}

	public class SocketBinder extends Binder {
		public SocketService getService() {
			return SocketService.this;
		}
	}

	public static Socket getSocket() {
		return ServerLink.getSocket();
	}

	public static ServerLink getServerLink() {
		return serverLink;
	}

	public static void setServerLink(ServerLink serverLink) {
		SocketService.serverLink = serverLink;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public void emit(String event, JSONObject data) {
		ServerLink.getSocket().emit(event, data);
	}
	public User getUser() {

		System.out.println("HEIN "+user.getPlaylists().size());
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
*/
}
