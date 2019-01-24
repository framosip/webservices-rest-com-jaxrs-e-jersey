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

		String produtoNovo = "<br.com.alura.loja.modelo.Produto><preco>60.0</preco><id>3467</id><nome>Jogo de esporte</nome><quantidade>1</quantidade></br.com.alura.loja.modelo.Produto>";
		
		Entity<String> entity = Entity.entity(produtoNovo, MediaType.APPLICATION_XML);
		
		Response response = target.path("/carrinhos/1/produtos/3467").request().put(entity);
		
		Assert.assertEquals(200, response.getStatus());
		
		Carrinho carrinho = new CarrinhoDAO().busca(1l);
		List<Produto> produtos = carrinho.getProdutos();
		
		for (Produto produto : produtos) {
			if(produto.getId() == 3467) {
				Assert.assertEquals(1, produto.getQuantidade());
			}
		}
		
	}
	
	@Test
	public void testaAlteracaoApenasDaQuantidadeDeUmProduto() {
		WebTarget target = client.target("http://localhost:8080");

		String produtoNovo = "<br.com.alura.loja.modelo.Produto><id>3467</id><quantidade>8</quantidade></br.com.alura.loja.modelo.Produto>";
		
		Entity<String> entity = Entity.entity(produtoNovo, MediaType.APPLICATION_XML);
		
		Response response = target.path("/carrinhos/1/produtos/3467/quantidade").request().put(entity);
		
		Assert.assertEquals(200, response.getStatus());
		
		Carrinho carrinho = new CarrinhoDAO().busca(1l);
		List<Produto> produtos = carrinho.getProdutos();
		
		for (Produto produto : produtos) {
			if(produto.getId() == 3467) {
				Assert.assertEquals(8, produto.getQuantidade());
			}
		}
		
	}	
	

	
}
