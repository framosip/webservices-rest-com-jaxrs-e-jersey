package br.com.alura.loja;

import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Projeto;
import junit.framework.Assert;

public class ProjetoTest {

	private HttpServer servidor;

	@Before
	public void iniciaServidor() throws URISyntaxException {
		servidor = Servidor.iniciaServidor();
	}
	
	@After
	public void paraServidor() {
		servidor.stop();
	}
	
	@Test
	public void retornaUmProjetoAcessandoResourceProjetos() {
		
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		String conteudo = target.path("/projetos").request().get(String.class);
		Projeto projeto = (Projeto) new XStream().fromXML(conteudo);
		
		Assert.assertEquals("Minha loja", projeto.getNome());
	}
	
}
