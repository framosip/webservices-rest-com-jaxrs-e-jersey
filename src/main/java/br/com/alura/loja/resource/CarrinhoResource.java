package br.com.alura.loja.resource;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.dao.CarrinhoDAO;
import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

@Path("carrinhos")
public class CarrinhoResource {

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String busca(@PathParam("id") long id) {
		Carrinho carrinho = new CarrinhoDAO().busca(id);
		return carrinho.toXML();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response adiciona(String conteudo) {
		Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
		new CarrinhoDAO().adiciona(carrinho);
		
		URI location = URI.create("/carrinhos/" + carrinho.getId());
		return Response.created(location).build();
	}
	
	@Path("{id}/produtos/{idProduto}")
	@DELETE
	public Response removeProduto(@PathParam("id") long idCarrinho, @PathParam("idProduto") long idProduto) {
		Carrinho carrinho = new CarrinhoDAO().busca(idCarrinho);
		carrinho.remove(idProduto);
		
		return Response.ok().build();
	}
	
	@Path("{id}/produtos/{idProduto}")
	@PUT
	public Response alteraProduto(String conteudo, @PathParam("id") long id, @PathParam("idProduto") long idProduto) {
		Carrinho carrinho = new CarrinhoDAO().busca(id);
		Produto produto = (Produto) new XStream().fromXML(conteudo);
		
		carrinho.troca(produto);
		
		return Response.ok().build();
	}
	
	@Path("{id}/produtos/{idProduto}/quantidade")
	@PUT
	public Response alteraQuantidadeProduto(String conteudo, @PathParam("id") long id, @PathParam("idProduto") long idProduto) {
		Carrinho carrinho = new CarrinhoDAO().busca(id);
		Produto produto = (Produto) new XStream().fromXML(conteudo);
		
		carrinho.trocaQuantidade(produto);
		
		return Response.ok().build();		
	}
	
}
