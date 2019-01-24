package br.com.alura.loja.modelo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Produto {

	private double preco;
	private long id;
	private String nome;
	private int quantidade;

	public Produto() {
	}
	
	public Produto(long id, String nome, double preco, int quantidade) {
		this.id = id;
		this.nome = nome;
		this.preco = preco;
		this.quantidade = quantidade;
	}

	public double getPreco() {
		return preco;
	}

	public long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public double getPrecoTotal() {
		return quantidade * preco;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Produto other = (Produto) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
