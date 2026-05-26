//package org.escolar.controller;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.annotation.ManagedBean;
//import javax.faces.bean.ViewScoped;
//
//import org.escolar.model.Aluno;
//import org.escolar.model.Boleto;
//import org.escolar.model.ContratoAluno;
//
//@ManagedBean
//@ViewScoped
//public class ContratoAlunoController implements Serializable {
//    /**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	private List<ContratoAluno> listaContratos;
//
//    // Construtor que cria a lista de contratos mockada
//    public ContratoAlunoController() {
//        listaContratos = new ArrayList<>();
//
//        // Criando contratos de exemplo (mockados)
//        for (int i = 1; i <= 5; i++) {
//            ContratoAluno contrato = new ContratoAluno();
//            contrato.setId(Long.parseLong(i+""));
//            contrato.setAluno(new Aluno());  // Nome fictício para o aluno
//            contrato.setAno((short) 2025);
//            contrato.setNomeResponsavel("Responsável " + i);
//            contrato.setCpfResponsavel("123.456.789-0");
//            contrato.setEndereco("Endereço " + i);
//            contrato.setValorMensal(500.00);
//            contrato.setNumeroParcelas(12);
//            contrato.setComentario("Comentário sobre o contrato " + i);
//            
//
//            // Criando boletos fictícios
//            List<Boleto> boletos = new ArrayList<>();
//            for (int j = 1; j <= 3; j++) {
//                Boleto boleto = new Boleto();
//                boleto.setNossoNumero(i);
//                boleto.setValorNominal(500.00 + (50.00 * j));
//                boleto.setVencimento(new Date());
//                boletos.add(boleto);
//            }
//
//            contrato.setBoletos(boletos);
//            listaContratos.add(contrato);
//        }
//    }
//
//    // Getter para a lista de contratos
//    public List<ContratoAluno> getListaContratos() {
//
//    	return listaContratos;
//    }
//
//    // Setter para a lista de contratos (caso necessário)
//    public void setListaContratos(List<ContratoAluno> listaContratos) {
//        this.listaContratos = listaContratos;
//    }
//}
