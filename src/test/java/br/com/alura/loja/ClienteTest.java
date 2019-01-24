package br.com.alura.loja;

import java.net.URISyntaxException;
import java.util.List;

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
		Carrinho carrinho = target.path("/carrinhos/1").request().get(Carrinho.class);
		
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}
	
	@Test
	public void testaAdicionarUmCarrinho() {
		WebTarget target = client.target("http://localhost:8080");
		
		Produto produto = new Produto(314L, "Tablet", 999, 1);
		
		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(produto);
        carrinho.setRua("Rua Vergueiro");
        carrinho.setCidade("Sao Paulo");
                
        Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);
        
        Response response = target.path("/carrinhos").request().post(entity);
        
        Assert.assertEquals(201, response.getStatus());
        
        String location = response.getHeaderString("Location");
        
        Carrinho carrinhoNovo = client.target(location).request().get(Carrinho.class);
        
        Assert.assertTrue(carrinhoNovo.getProdutos().contains(produto));
        
	}
	
	@Test
	public void testaQueProdutoFoiRemovido() {
		WebTarget target = client.target("http://localhost:8080");
		
		Response response = target.path("/carrinhos/1/produtos/6237").request().delete();
		
		Assert.assertEquals(200, response.getStatus());
		
		Carrinho carrinho = new CarrinhoDAO().busca(1l);
		
		boolean naoExiste = true;
		
		for (Produto p : carrinho.getProdutos()) {
			if(p.getId() == 6237)
				naoExiste = false;
		}
		
		Assert.assertTrue(naoExiste);
	}
	
	@Test
	public void testaAlteracaoDeUmProdutoInteiro() {
		WebTarget target = client.target("http://localhost:8080");

		Produto produto = new Produto(3467, "Jogo de esporte", 60.0, 1);
		
		Entity<Produto> entity = Entity.entity(produto, MediaType.APPLICATION_XML);
		
		Response response = target.path("/carrinhos/1/produtos/3467").request().put(entity);
		
		Assert.assertEquals(200, response.getStatus());
		
		Carrinho carrinho = new CarrinhoDAO().busca(1l);
		List<Produto> produtos = carrinho.getProdutos();
		
		for (Produto p : produtos) {
			if(p.getId() == 3467) {
				Assert.assertEquals(1, p.getQuantidade());
			}
		}
		
	}
	
	@Test
	public void testaAlteracaoApenasDaQuantidadeDeUmProduto() {
		WebTarget target = client.target("http://localhost:8080");

		Produto produto = new Produto(3467, "Jogo de esporte", 60.0, 8);
		
		Entity<Produto> entity = Entity.entity(produto, MediaType.APPLICATION_XML);
		
		Response response = target.path("/carrinhos/1/produtos/3467/quantidade").request().put(entity);
		
		Assert.assertEquals(200, response.getStatus());
		
		Carrinho carrinho = new CarrinhoDAO().busca(1l);
		List<Produto> produtos = carrinho.getProdutos();
		
		for (Produto p : produtos) {
			if(p.getId() == 3467) {
				Assert.assertEquals(8, p.getQuantidade());
			}
		}
		
	}	
	

	
}
