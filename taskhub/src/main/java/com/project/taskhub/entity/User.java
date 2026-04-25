package com.project.taskhub.entity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User extends TaskBase implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O email é obrigatório.")
    private String email;

    @NotBlank(message = "É necessário uma definição de senha..")
    private String password;

    private boolean inativo;

    public User(@NotBlank(message = "O nome é obrigatório.") String nome, @NotBlank(message = "O email é obrigatório.") String email,
	    @NotBlank(message = "É necessário uma definição de senha..") String password, @NotBlank(message = "OStaus é obrigatório.") boolean inativo) {
	this.nome = nome;
	this.email = email;
	this.password = password;
	this.inativo = inativo;
    }

    public User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
	return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
	return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
	return true;
    }

    @Override
    public boolean isAccountNonLocked() {
	return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
	return true;
    }

    @Override
    public boolean isEnabled() {
	// return !this.inativo;
	return true;
    }

    public String getNome() {
	return nome;
    }

    public void setNome(String nome) {
	this.nome = nome;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public boolean isInativo() {
	return inativo;
    }

    public void setInativo(boolean inativo) {
	this.inativo = inativo;
    }

    public Long getId() {
	return id;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	User other = (User) obj;
	return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
	return "User [id=" + id + ", nome=" + nome + ", email=" + email + ", password=" + password + ", inativo=" + inativo + "]";
    }

}
