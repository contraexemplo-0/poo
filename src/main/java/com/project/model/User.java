package com.project.model;

/**
 * Classe base abstrata que representa um usuário do sistema.
 *
 * <p>No MVP, apenas {@link Patient} estende esta classe, porém sua existência
 * permite evolução futura para múltiplos papéis, como médicos, cuidadores ou
 * administradores.</p>
 *
 * <p>Contém informações essenciais de autenticação e identificação.</p>
 */
public abstract class User {

    /** Identificador único do usuário no banco de dados. */
    private int id;

    /** Nome do usuário a ser exibido na interface. */
    private String name;

    /** Senha do usuário (armazenada em texto simples no MVP). */
    private String password;

    /**
     * Construtor protegido para impedir instanciação direta e permitir apenas subclasses.
     *
     * @param id identificador único do usuário
     * @param name nome do usuário
     * @param password senha em texto simples
     */
    protected User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    /** @return identificador do usuário */
    public int getId() {
        return id;
    }

    /** @param id novo identificador do usuário */
    public void setId(int id) {
        this.id = id;
    }

    /** @return nome do usuário */
    public String getName() {
        return name;
    }

    /** @param name novo nome do usuário */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retorna a senha em texto simples.
     *
     * <p>No MVP é armazenada sem hashing; em versões futuras deve ser substituída
     * por mecanismos seguros de autenticação.</p>
     *
     * @return senha do usuário
     */
    public String getPassword() {
        return password;
    }

    /**
     * Verifica se uma senha fornecida corresponde à senha armazenada.
     *
     * @param input senha de entrada
     * @return {@code true} se a senha for igual
     */
    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    /** @param password nova senha do usuário */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Retorna um texto breve com a identificação do usuário, usado em interfaces gerais.
     *
     * @return resumo textual do usuário
     */
    public String getSummary() {
        return "Usuário: " + name;
    }

    /**
     * Retorna uma mensagem de boas-vindas utilizada no dashboard.
     *
     * @return texto personalizado para a tela principal
     */
    public String getDashboardSummary() {
        return "Bem-vindo, " + name + ".";
    }
}