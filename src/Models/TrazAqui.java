package Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TrazAqui
{
    private Map<String,Loja> lojas;
    private Map<String,Voluntario> voluntarios;
    private Map<String,Transportadora> transportadoras;
    private Map<String,Utilizador> utilizadores;

    private Map<String,Encomenda> catalogoEncomendas;

    private List<String> aceites; //Inútil???
    private String utilizador_atual;


    public TrazAqui() {
        this.lojas = new TreeMap<>();
        this.voluntarios = new TreeMap<>();
        this.transportadoras = new TreeMap<>();
        this.utilizadores = new TreeMap<>();
        this.catalogoEncomendas = new TreeMap<>();
        this.aceites = new ArrayList<>();
        this.utilizador_atual = "";

    }

    public void insereLoja(Loja l)
    {
        this.lojas.put(l.getCodigo(), l.clone());
    }

    public void insereVoluntario(Voluntario v)
    {
        this.voluntarios.put(v.getCodigo(), v.clone());
    }

    public void insereTransportadora(Transportadora t)
    {
        this.transportadoras.put(t.getCodigo(), t.clone());
    }

    public void insereUtilizador(Utilizador u)
    {
        this.utilizadores.put(u.getCodigo(), u.clone());
    }

    public void insereEncomendaAceite(String e)
    {
        this.aceites.add(e);
        this.catalogoEncomendas.get(e).setAceiteLoja(true);
        this.utilizadores.values().forEach(val -> val.verificaPossuiVendaeRemovePendente(e));
    }

    public void insereEncomenda(Encomenda e, String codLoja)
    {
        this.lojas.get(codLoja).insereEncomenda(e);
        this.catalogoEncomendas.putIfAbsent(e.getCodigo(), e.clone());
    }

    public void adicionaEncomendaAoSistema(Encomenda e)
    {
        this.lojas.get(e.getCodLoja()).insereEncomenda(e);
        this.utilizadores.get(e.getCodUtilizador()).insereEncomenda(e);
        this.catalogoEncomendas.putIfAbsent(e.getCodigo(), e.clone());
    }

    public void setUtilizador_atual(String utilizador)
    {
        this.utilizador_atual = utilizador;
    }

    public String getUtilizador_atual()
    {
        return utilizador_atual;
    }

    public List<Loja> getLojas()
    {
        return this.lojas.values().stream().map(Loja::clone).collect(Collectors.toList());
    }

    public Loja getLoja(String codLoja)
    {
        return this.lojas.get(codLoja).clone();
    }

    public Voluntario getVoluntario(String codVoluntario)
    {
        return this.voluntarios.get(codVoluntario).clone();
    }

    public Utilizador getUtilizador(String codUtilizador)
    {
        return this.utilizadores.get(codUtilizador).clone();
    }

    public Transportadora getTransportador(String codTransportadora)
    {
        return this.transportadoras.get(codTransportadora).clone();
    }

    public List<Voluntario> getVoluntarios()
    {
        return this.voluntarios.values().stream().map(Voluntario::clone).collect(Collectors.toList());
    }

    public List<Transportadora> getTransportadoras()
    {
        return this.transportadoras.values().stream().map(Transportadora::clone).collect(Collectors.toList());
    }

    public List<Utilizador> getUtilizadores()
    {
        return this.utilizadores.values().stream().map(Utilizador::clone).collect(Collectors.toList());
    }

    public List<String> getEncomendasAceites()
    {
        return new ArrayList<>(this.aceites);
    }

    public Map<String, Encomenda> getCatalogoEncomendas () {
        return this.catalogoEncomendas
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e->e.getValue().clone()));
    }

    public Encomenda getEncomenda (String codEncomenda) {
        if (this.catalogoEncomendas.containsKey(codEncomenda)) {
            return this.catalogoEncomendas.get(codEncomenda).clone();
        }
        return null;
    }

    public boolean procuraEncomendaAceite(String codigo)
    {
        return this.aceites.contains(codigo);
    }

    public boolean procuraUtilizador(String utilizador)
    {
        return this.utilizadores.containsKey(utilizador);
    }

    public boolean procuraVoluntario(String voluntario)
    {
        return this.voluntarios.containsKey(voluntario);
    }

    public boolean procuraTransportadora(String transportadora)
    {
        return this.transportadoras.containsKey(transportadora);
    }

    public boolean procuraLoja(String loja)
    {
        return this.lojas.containsKey(loja);
    }

    public boolean verificaPassword(String utilizador, String password)
    {
        Utilizador u = this.utilizadores.get(utilizador);

        return u.getPassword().equals(password);
    }

    public String realizaEntregaDeVenda(String codLoja, String codEnc, String codVoluntario) {

        StringBuilder sb = new StringBuilder();
        Encomenda enc = this.getEncomenda(codEnc);

        this.getLoja(codLoja).realizaEntregaDeVenda(enc);//Done
        this.getVoluntario(codVoluntario).realizaEntregaDeVenda(enc, this.lojas.get(codLoja), this.getUtilizador(enc.getCodUtilizador()));
        this.getUtilizador(enc.getCodUtilizador()).realizaEntregaDeVenda(enc);

        this.catalogoEncomendas.put(codEnc, enc); //Replace da Encomenda antiga para n partilhar apontadores e ser sempre cópias

        sb.append("Tempo demorado a realizar a entrega -> ")
                .append((int) this.catalogoEncomendas.get(enc.getCodigo()).getTempoTransporte()/60).append(" Horas e ")
                .append((int) this.catalogoEncomendas.get(enc.getCodigo()).getTempoTransporte()%60).append(" minutos ");

        if(this.catalogoEncomendas.get(enc.getCodigo()).getCondicoesClimatericas() == 0)
            sb.append("em condições Normais\n");
        else if(this.catalogoEncomendas.get(enc.getCodigo()).getCondicoesClimatericas() == 1)
            sb.append("em condições de chuva\n");
        else if(this.catalogoEncomendas.get(enc.getCodigo()).getCondicoesClimatericas() == 2)
            sb.append("em condições de Neve e Tempestade\n");

        return sb.toString();
    }


    public void setAvailable (String codVoluntario, boolean status) {
        this.voluntarios.get(codVoluntario).setAvailable(status);
    }
}
