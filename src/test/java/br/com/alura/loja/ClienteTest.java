package br.com.alura.loja;

import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.dao.CarrinhoDAO;
import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import junit.framework.Assert;

public class ClienteTest {

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
	public void finalizaServidor() {
		servidor.stop();
	}
	
	@Test
	public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
		WebTarget target = client.target("http://localhost:8080");
		
		String conteudo = target.path("/carrinhos/1").request().get(String.class);
		Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
		
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}
	
	@Test
	public void testaAdicionarUmCarrinho() {
		WebTarget target = client.target("http://localhost:8080");
		
		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(new Produto(314L, "Tablet", 999, 1));
        carrinho.setRua("Rua Vergueiro");
        carrinho.setCidade("Sao Paulo");
        
        String xml = carrinho.toXML();
        
        Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
        
        Response response = target.path("/carrinhos").request().post(entity);
        
        Assert.assertEquals(201, response.getStatus());
        
        String location = response.getHeaderString("Location");
        
        String conteudo = client.target(location).request().get(String.class);
        
        Assert.assertTrue(conteudo.contains("Tablet"));
        
	}
	
	@Test
	public void testaQueProdutoFoiRemovido() {
		WebTarget target = client.target("http://localhost:8080");
		
		Response response = target.path("/carrinhos/1/remover/6237").request().delete();
		
		Assert.assertEquals(200, response.getStatus());
		
		Carrinho carrinho = new CarrinhoDAO().busca(1l);
		
		boolean naoExiste = true;
		
		for (Produto p : carrinho.getProdutos()) {
			if(p.getId() == 6237)
				naoExiste = false;
		}
		
		Assert.assertTrue(naoExiste);
		
		
	}
	
}
