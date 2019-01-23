package br.com.alura.loja;

import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Projeto;
import junit.framework.Assert;

public class ProjetoTest {

	private HttpServer servidor;
	private Client client;

	@Before
	public void iniciaServidor() throws URISyntaxException {
		servidor = Servidor.iniciaServidor();
		
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		
		this.client = ClientBuilder.newClient(config);
	}
	
	@After
	public void paraServidor() {
		servidor.stop();
	}
	
	@Test
	public void retornaUmProjetoAcessandoResourceProjetos() {
		WebTarget target = client.target("http://localhost:8080");
		String conteudo = target.path("/projetos/2").request().get(String.class);
		Projeto projeto = (Projeto) new XStream().fromXML(conteudo);
		
		Assert.assertEquals("Alura", projeto.getNome());
	}
	
	@Test
	public void testaExcluirUmProjetoPorId() {
		WebTarget target = client.target("http://localhost:8080");
		Response response = target.path("/projetos/1").request().delete();
		
		Assert.assertEquals(200, response.getStatus());
	}
	
}
