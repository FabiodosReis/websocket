package br.com.webSocket;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * 
 * @author Fabio
 * essa classe faz um servidor websocket para fazer um canal continuo
 * entre um cliente e servidor que precise que o servidor fique enviando mensagens a cada
 * x tempo(s)
 */

@ServerEndpoint(value="/ws")
public class Sender {	
	
	private CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<Session>();	
	private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
	
	Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	
	@OnOpen
	public void onOpen(Session session) {
		
		logger.info("Sesão aberta " + session.getId());		
		
		sessions.add(session);		
		
		if(sessions.size() == 1) 			
			timer.scheduleAtFixedRate(() -> onMessage(session), 0, 1, TimeUnit.SECONDS);
		
	}
	
	@OnClose
	public void onClose(Session session) {
		
		logger.info("Sesão fechada " + session.getId());
		
		sessions.remove(session);
		
	}	
	
	public void onMessage(Session session) {
		
		sessions.stream().forEach(s -> {
			
			try {
				
				//do something				
				s.getBasicRemote().sendText("teste\n");
				
			} catch (Exception e) {
				
				System.out.println(e.getMessage());
				onError(e, session);
				
			}
			
		});				
	}	
	
	@OnError
	public void onError(Throwable t, Session session) {
		
		logger.info("Erro no servidor: " + t.getMessage());		
				
		onClose(session);	
		
	}	
	
}
