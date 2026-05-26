package org.aaf.webInterface.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.aaf.escolar.AlunoDTO;
import org.aaf.escolar.BoletoDTO;
import org.aaf.escolar.ContratoAlunoDTO;
import org.escolar.enums.TipoMembro;
import org.escolar.model.Aluno;
import org.escolar.model.Boleto;
import org.escolar.model.Member;
import org.escolar.service.AlunoService;
import org.escolar.service.MemberRegistration;
import org.escolar.util.Verificador;

@Path("/students")
@RequestScoped
@Stateful
public class AlunoRest {

    @EJB
    private AlunoService alunoService;

    @EJB
    private MemberRegistration memberRegistration;
    
    
    
    @POST
    @Path("/boleto-aberto")
    @Consumes("application/json")
    @Produces("application/json")
    public Response getBoletoAberto(String body) {

        try {

            @SuppressWarnings("unchecked")
            Map<String, Object> json =
                    (Map<String, Object>)
                            com.cedarsoftware.util.io.JsonReader.jsonToJava(body);

            String cpf = (String) json.get("cpf");

            if (cpf == null || cpf.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Collections.singletonMap("erro", "CPF obrigatorio"))
                        .type("application/json")
                        .build();
            }

            // limpa CPF
            cpf = normalizarCPF(cpf);

            // valida CPF
            if (!isCPFValido(cpf)) {
                return Response.ok(Collections.singletonMap("status", "cpf_invalido"))
                        .type("application/json")
                        .build();
            }

            List<Aluno> alunos = alunoService.findAluno(null, null, cpf, null);

            if (alunos == null || alunos.isEmpty()) {
                return Response.ok(Collections.singletonMap("status", "cpf_nao_encontrado"))
                        .type("application/json")
                        .build();
            }

            Aluno aluno = alunos.get(0);

            List<ContratoAlunoDTO> contratos =
                    alunoService.findContratosAtivos(aluno.getId());

            Boleto boletoEncontrado = null;

            // procura boleto em aberto
            for (ContratoAlunoDTO contrato : contratos) {

                List<Boleto> boletos =
                        alunoService.findBoletosByIdAlgumContratoATivo(
                                contrato.getIdContrato());

                if (boletos == null) continue;

                for (Boleto b : boletos) {

                    String status = Verificador.getStatus(b);

                    if ("A Vencer".equalsIgnoreCase(status) || "Atrasado".equalsIgnoreCase(status) || "Vence Hoje".equalsIgnoreCase(status) || "Renegociado ".equalsIgnoreCase(status) || "Renegociado atrasado".equalsIgnoreCase(status)) {
                        boletoEncontrado = b;
                        break;
                    }
                }

                if (boletoEncontrado != null)
                    break;
            }

            if (boletoEncontrado == null) {

                return Response.ok(Collections.singletonMap(
                        "status",
                        "sem_boletos_abertos"))
                        .type("application/json")
                        .build();
            }

            Map<String, Object> boleto = new HashMap<>();

            boleto.put("id", boletoEncontrado.getId());
            boleto.put("valor", boletoEncontrado.getValorNominal());

            String vencimento = "";

            if (boletoEncontrado.getVencimento() != null) {
                vencimento = new SimpleDateFormat("yyyy-MM-dd")
                        .format(boletoEncontrado.getVencimento());
            }

            boleto.put("vencimento", vencimento);

            boleto.put(
                    "pdf",
                    "http://tefamel.ddns.net:1717/EscolarWebService/rest/students/boleto/"
                            + boletoEncontrado.getId()
            );

            Map<String, Object> response = new HashMap<>();

            response.put("status", "ok");
            response.put("nome", aluno.getNomeAluno());
            response.put("boleto", boleto);

            return Response.ok(response)
                    .type("application/json")
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("erro", e.getMessage()))
                    .type("application/json")
                    .build();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    

    // -----------------------------------------------------------------------
    // GET /students?memberId={id}  — corrige o bug da lista vazia
    // -----------------------------------------------------------------------
    @GET
    @Produces("application/json")
    public Response listarAlunos(@QueryParam("memberId") Long memberId) {
        try {
            if (memberId == null)
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"erro\":\"memberId obrigatorio\"}").build();

            Member member = memberRegistration.findById(memberId);
            if (member == null)
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\":\"Membro nao encontrado\"}").build();

            List<AlunoDTO> resultado = new ArrayList<>();

            // member.getTipoMembro() retorna TipoMembro (enum) — confirmado no Member.java
            TipoMembro tipo = member.getTipoMembro();

            if (tipo == TipoMembro.MOTORISTA || tipo == TipoMembro.MONITOR) {
                // findAlunosByMemberCarro() adicionado ao AlunoService — ver AlunoService.java
                resultado = alunoService.findAlunosByMemberCarro(memberId);

            } else if (tipo == TipoMembro.ALUNO) {
                // member.getIdCrianca1() retorna String — confirmado no Member.java
                List<Long> ids = new ArrayList<>();
                adicionarSeValido(ids, member.getIdCrianca1());
                adicionarSeValido(ids, member.getIdCrianca2());
                adicionarSeValido(ids, member.getIdCrianca3());
                adicionarSeValido(ids, member.getIdCrianca4());
                adicionarSeValido(ids, member.getIdCrianca5());
                resultado = alunoService.findAlunosByIds(ids);

            } else {
                // Admin / Secretaria / Dono
                // findAlunoDoAnoLetivo() retorna List<Aluno> — confirmado no AlunoService.java
                for (Aluno a : alunoService.findAlunoDoAnoLetivo())
                    resultado.add(alunoService.toDTO(a));
            }

            return Response.ok(
                    com.cedarsoftware.util.io.JsonWriter.objectToJson(resultado)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // GET /students/{idaluno}
    // -----------------------------------------------------------------------
    @GET 
    @Path("/{idaluno:\\d+}")
    @Produces("application/json")
    public Response getAlunoById(@PathParam("idaluno") String idaluno) {
        try {
            // findById(Long id) — confirmado no AlunoService.java
            Aluno aluno = alunoService.findById(Long.parseLong(idaluno));
            if (aluno == null)
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\":\"Aluno nao encontrado\"}").build();
            // toDTO() adicionado ao AlunoService — ver AlunoService.java
            return Response.ok(
                    com.cedarsoftware.util.io.JsonWriter.objectToJson(
                            alunoService.toDTO(aluno))).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // GET /students/boletos/{idContrato}
    // -----------------------------------------------------------------------
    @GET @Path("/boletos/{idContrato}") @Produces("application/json")
    public Response getBoletos(@PathParam("idContrato") String idContrato) {
        try {
            // findBoletosByIdAlgumContratoATivo() retorna List<Boleto> — confirmado no AlunoService.java
            List<Boleto> boletos = alunoService.findBoletosByIdAlgumContratoATivo(
                    Long.parseLong(idContrato));
            List<BoletoDTO> dtos = new ArrayList<>();
            for (Boleto b : boletos) dtos.add(toBoletoDTO(b));
            return Response.ok(
                    com.cedarsoftware.util.io.JsonWriter.objectToJson(dtos)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // GET /students/boleto/{idboleto}  — PDF
    // -----------------------------------------------------------------------
    @GET @Path("/boleto/{idboleto}") @Produces("application/pdf")
    public Response getBoletoPDF(@PathParam("idboleto") String idboleto) {
        try {
            // byteArrayPDFBoleto(Long) — confirmado no AlunoService.java
            byte[] pdf = alunoService.byteArrayPDFBoleto(Long.parseLong(idboleto));
            return Response.ok(pdf).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // GET /students/contratos/{idAluno}
    // -----------------------------------------------------------------------
    @GET @Path("/contratos/{idAluno}") @Produces("application/json")
    public Response getContratosAluno(@PathParam("idAluno") String idAluno) {
        try {
            // findContratosAtivos(Long) retorna List<ContratoAlunoDTO> — confirmado no AlunoService.java
            // NÃO é List<ContratoAluno> — já converte internamente via getDTO()
            List<ContratoAlunoDTO> dtos = alunoService.findContratosAtivos(
                    Long.parseLong(idAluno));
            return Response.ok(
                    com.cedarsoftware.util.io.JsonWriter.objectToJson(dtos)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // GET /students/declaracaoIR/{idcontrato}
    // -----------------------------------------------------------------------
    @GET @Path("/declaracaoIR/{idcontrato}") @Produces("application/pdf")
    public Response getPDFDeclaracaoIR(@PathParam("idcontrato") String idcontrato) {
        try {
            // byteArrayPDFDeclaracaoIR(Long) — confirmado no AlunoService.java
            byte[] pdf = alunoService.byteArrayPDFDeclaracaoIR(Long.parseLong(idcontrato));
            return Response.ok(pdf).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // PUT /students/{id}/contatos
    // -----------------------------------------------------------------------
    @PUT @Path("/{id}/contatos") @Consumes("application/json") @Produces("application/json")
    public Response atualizarContatos(@PathParam("id") Long idAluno,
                                      @QueryParam("memberId") Long memberId,
                                      String body) {
        try {
            if (!isFuncionario(memberId))
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"erro\":\"Apenas funcionarios podem editar contatos\"}").build();

            Aluno aluno = alunoService.findById(idAluno);
            if (aluno == null)
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\":\"Aluno nao encontrado\"}").build();

            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> c =
                    (java.util.Map<String, Object>)
                            com.cedarsoftware.util.io.JsonReader.jsonToJava(body);

            // Todos os setters são String ou Boolean — confirmado no Aluno.java
            if (c.get("contatoNome1")        != null) aluno.setContatoNome1((String)  c.get("contatoNome1"));
            if (c.get("contatoTelefone1")    != null) aluno.setContatoTelefone1((String) c.get("contatoTelefone1"));
            if (c.get("contato1WhatsValido") != null) aluno.setContato1WhatsValido((Boolean) c.get("contato1WhatsValido"));
            if (c.get("contatoNome2")        != null) aluno.setContatoNome2((String) c.get("contatoNome2"));
            if (c.get("contatoTelefone2")    != null) aluno.setContatoTelefone2((String) c.get("contatoTelefone2"));
            if (c.get("contato2WhatsValido") != null) aluno.setContato2WhatsValido((Boolean) c.get("contato2WhatsValido"));
            if (c.get("contatoNome3")        != null) aluno.setContatoNome3((String) c.get("contatoNome3"));
            if (c.get("contatoTelefone3")    != null) aluno.setContatoTelefone3((String) c.get("contatoTelefone3"));
            if (c.get("contato3WhatsValido") != null) aluno.setContato3WhatsValido((Boolean) c.get("contato3WhatsValido"));
            if (c.get("contatoEmail1")       != null) aluno.setContatoEmail1((String) c.get("contatoEmail1"));
            if (c.get("contatoEmail2")       != null) aluno.setContatoEmail2((String) c.get("contatoEmail2"));

            // saveAluno(Aluno, boolean) — REQUER 2 parâmetros — confirmado no AlunoService.java
            // false = não propagar para irmãos
            alunoService.saveAluno(aluno, false);
            return Response.ok("{\"status\":\"ok\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // PUT /students/{id}/carro
    // -----------------------------------------------------------------------
    @PUT @Path("/{id}/carro") @Consumes("application/json") @Produces("application/json")
    public Response atualizarCarro(@PathParam("id") Long idAluno,
                                   @QueryParam("memberId") Long memberId,
                                   String body) {
        try {
            if (!isFuncionario(memberId))
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"erro\":\"Apenas funcionarios podem alterar o carro\"}").build();

            Aluno aluno = alunoService.findById(idAluno);
            if (aluno == null)
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\":\"Aluno nao encontrado\"}").build();

            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> c =
                    (java.util.Map<String, Object>)
                            com.cedarsoftware.util.io.JsonReader.jsonToJava(body);

            // findCarroById() adicionado ao AlunoService — ver AlunoService.java
            if (c.get("idCarroIda") != null) {
                org.escolar.model.Carro carro = alunoService.findCarroById(
                        Long.parseLong(c.get("idCarroIda").toString()));
                if (carro != null) aluno.setCarroLevaParaEscola(carro);
            }
            if (c.get("idCarroVolta") != null) {
                org.escolar.model.Carro carro = alunoService.findCarroById(
                        Long.parseLong(c.get("idCarroVolta").toString()));
                if (carro != null) aluno.setCarroPegaEscola(carro);
            }

            alunoService.saveAluno(aluno, false);
            return Response.ok("{\"status\":\"ok\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // GET /students/boleto/{idboleto}/pix
    // -----------------------------------------------------------------------
    @GET @Path("/boleto/{idboleto}/pix") @Produces("application/json")
    public Response getPixInfo(@PathParam("idboleto") Long idBoleto) {
        try {
            // findBoletoById() confirmado no AlunoService.java
            Boleto boleto = alunoService.findBoletoById(idBoleto);
            if (boleto == null)
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\":\"Boleto nao encontrado\"}").build();

            // getPixKeyEmpresa() adicionado ao AlunoService
            String pixKey = alunoService.getPixKeyEmpresa();

            String nomePag = boleto.getPagador() != null
                    && boleto.getPagador().getNomeAluno() != null
                    ? boleto.getPagador().getNomeAluno() : "";

            String dateFmt = boleto.getVencimento() != null
                    ? new SimpleDateFormat("yyyy-MM-dd").format(boleto.getVencimento()) : "";

            // Verificador.getStatus(Boleto) — confirmado no Verificador.java
            String status = Verificador.getStatus(boleto);

            return Response.ok(
                    "{\"linhaDigitavel\":\"" + String.valueOf(boleto.getNossoNumero()) + "\","
                    + "\"pixKey\":\"" + pixKey + "\","
                    + "\"valor\":" + boleto.getValorNominal() + ","
                    + "\"vencimento\":\"" + dateFmt + "\","
                    + "\"nomePagador\":\"" + nomePag + "\","
                    + "\"status\":\"" + status + "\"}"
            ).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // Helpers privados
    // -----------------------------------------------------------------------

    /**
     * Verifica se o membro é funcionário (motorista, monitor, secretaria, admin, dono).
     * member.getTipoMembro() retorna TipoMembro (enum) — confirmado no Member.java
     */
    private boolean isFuncionario(Long memberId) {
        if (memberId == null) return false;
        Member m = memberRegistration.findById(memberId);
        if (m == null) return false;
        TipoMembro t = m.getTipoMembro();
        return t == TipoMembro.MOTORISTA || t == TipoMembro.MONITOR
                || t == TipoMembro.SECRETARIA || t == TipoMembro.ADMIM
                || t == TipoMembro.DONO;
    }

    /**
     * Converte String para Long e adiciona à lista, ignorando nulos e inválidos.
     * Necessário porque member.getIdCrianca1() retorna String — confirmado no Member.java
     */
    private void adicionarSeValido(List<Long> lista, String valor) {
        if (valor != null && !valor.isEmpty()) {
            try { lista.add(Long.parseLong(valor)); }
            catch (NumberFormatException ignored) {}
        }
    }

    /**
     * Boleto (model) → BoletoDTO.
     *
     * Tipos confirmados nos .java fonte:
     *   Boleto.getVencimento()     → Date   ✓
     *   Boleto.getDataPagamento()  → Date   ✓
     *   Boleto.getValorNominal()   → double ✓
     *   Boleto.getValorPago()      → Double ✓
     *   Boleto.getCancelado()      → Boolean ✓
     *   Boleto.getProtestado()     → Boolean ✓  (setProtestado(Boolean) — NÃO String)
     *   Boleto.getNossoNumero()    → long primitivo ✓
     *   BoletoDTO.setLinhaDigitavel(String) — nossoNumero como fallback
     *   Verificador.getStatus(Boleto) → String ✓ — confirmado no Verificador.java
     */
    private BoletoDTO toBoletoDTO(Boleto b) {
        BoletoDTO dto = new BoletoDTO();
        dto.setId(b.getId());
        dto.setVencimento(b.getVencimento());
        dto.setDataPagamento(b.getDataPagamento());
        dto.setValorNominal(b.getValorNominal());
        dto.setValorPago(b.getValorPago());
        dto.setCancelado(b.getCancelado());
        // setProtestado(Boolean) — confirmado no BoletoDTO.java — NÃO String
        dto.setProtestado(b.getProtestado());
        // Boleto model não tem linhaDigitavel — usar nossoNumero como fallback
        dto.setLinhaDigitavel(String.valueOf(b.getNossoNumero()));
        // Verificador.getStatus(Boleto) — confirmado no Verificador.java
        dto.setStatusBoleto(Verificador.getStatus(b));
        return dto;
    }

    
    @POST
    @Path("/consulta-cpf")
    @Consumes("application/json")
    @Produces("application/json")
    public Response consultarPorCpf(String body) {
        try {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> json =
                    (java.util.Map<String, Object>)
                            com.cedarsoftware.util.io.JsonReader.jsonToJava(body);

            String cpf = (String) json.get("cpf");

            if (cpf == null || cpf.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Collections.singletonMap("erro", "CPF obrigatorio"))
                        .type("application/json")
                        .build();
            }

            // limpa cpf
            cpf = normalizarCPF(cpf);

            // valida CPF
            if (!isCPFValido(cpf)) {
                return Response.ok(Collections.singletonMap("status", "cpf_invalido"))
                        .type("application/json")
                        .build();
            }

            List<Aluno> alunos = alunoService.findAluno(null, null, cpf, null);

            if (alunos == null || alunos.isEmpty()) {
                return Response.ok(Collections.singletonMap("status", "cpf_nao_encontrado"))
                        .type("application/json")
                        .build();
            }

            Aluno aluno = alunos.get(0);

            List<ContratoAlunoDTO> contratos =
                    alunoService.findContratosAtivos(aluno.getId());

            // monta resposta limpa
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ok");
            response.put("nome", aluno.getNomeAluno());
            response.put("idAluno", aluno.getId());
            response.put("qtdContratos", contratos.size());

            List<Map<String, Object>> listaContratos = new ArrayList<>();

            for (ContratoAlunoDTO c : contratos) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", c.getIdContrato());
                item.put("ano", c.getAno());
                item.put("valor", c.getValorMensal());
                listaContratos.add(item);
            }

            response.put("contratos", listaContratos);

            return Response.ok(response)
                    .type("application/json")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("erro", e.getMessage()))
                    .type("application/json")
                    .build();
        }
    }
    
    private boolean isCPFValido(String cpf) {
        if (cpf == null || cpf.length() != 11) return false;

        // evita CPFs tipo 11111111111
        if (cpf.chars().distinct().count() == 1) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++)
                soma += (cpf.charAt(i) - '0') * (10 - i);

            int dig1 = 11 - (soma % 11);
            if (dig1 >= 10) dig1 = 0;

            soma = 0;
            for (int i = 0; i < 10; i++)
                soma += (cpf.charAt(i) - '0') * (11 - i);

            int dig2 = 11 - (soma % 11);
            if (dig2 >= 10) dig2 = 0;

            return dig1 == (cpf.charAt(9) - '0')
                    && dig2 == (cpf.charAt(10) - '0');

        } catch (Exception e) {
            return false;
        }
    }
    
    private String normalizarCPF(String cpf) {
        if (cpf == null) return null;

        // remove tudo que não for número
        return cpf.replaceAll("[^0-9]", "");
    }
}
