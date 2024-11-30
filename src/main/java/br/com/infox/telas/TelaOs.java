/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.infox.telas;
import java.sql.*;
import br.com.infox.dal.ModuloConexao;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.*;



/**
 *
 * @author jaque
 */
public class TelaOs extends javax.swing.JInternalFrame {
Connection conexao = null;
PreparedStatement pst = null;
ResultSet rs = null;

    private String tipo;
    /**
     * Creates new form TelaOs
     */
    public TelaOs() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
private void pesquisarClientes() {
        String sql = "SELECT idcli, nomecli, fonecli FROM tbclientes WHERE nomecli LIKE ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
            model.setNumRows(0); // Limpa as linhas da tabela

       
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("idcli"),
                    rs.getString("nomecli"),
                    rs.getString("fonecli")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


private void emitir_os() {
    String sql = "INSERT INTO tbos (tipo, situacao, equipamento, defeito, servico, tecnico, valor, idcli) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try {
        pst = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pst.setString(1, tipo);
        pst.setString(2, cboOsSt.getSelectedItem().toString());
        pst.setString(3, txtOsEquip.getText());
        pst.setString(4, txtOsDef.getText());
        pst.setString(5, txtOsServi.getText());
        pst.setString(6, txtOsTec.getText());
        pst.setString(7, txtOsValor.getText().replace(",", "."));
        pst.setString(8, txtCliId.getText());

        if ((txtCliId.getText().isEmpty()) || (txtOsEquip.getText().isEmpty()) || (txtOsDef.getText().isEmpty())) {
            JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
        } else {
            int adicionado = pst.executeUpdate();
            if (adicionado > 0) {
                // Recupera o ID gerado
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idOsGerada = generatedKeys.getInt(1); // Obtém o ID gerado
                    JOptionPane.showMessageDialog(null, "OS emitida com sucesso. Número da OC: " + idOsGerada);
                }
                // Limpa os campos
                txtCliId.setText(null);
                txtOsEquip.setText(null);
                txtOsDef.setText(null);
                txtOsServi.setText(null);
                txtOsTec.setText(null);
                txtOsValor.setText(null);
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e);
        e.printStackTrace();
    }
}


private void pesquisar_os() {
    String num_os = JOptionPane.showInputDialog("Número da OS");

    if (num_os == null) {
        return;
    }

    String sql = "SELECT O.*, C.nomecli FROM tbos AS O "
                 + "INNER JOIN tbclientes AS C ON O.idcli = C.idcli "
                 + "WHERE O.os = ?";

    try {
        pst = conexao.prepareStatement(sql);
        pst.setString(1, num_os);
        rs = pst.executeQuery();

        if (rs.next()) {
            // Preenchendo os campos da interface
            txtOs.setText(rs.getString("os"));
            txtData.setText(rs.getString("data_os"));
            txtOsEquip.setText(rs.getString("equipamento"));
            txtOsDef.setText(rs.getString("defeito"));
            txtOsServi.setText(rs.getString("servico"));
            txtOsTec.setText(rs.getString("tecnico"));
            txtOsValor.setText(rs.getString("valor"));
            txtCliId.setText(rs.getString("idcli"));
            txtCliPesquisar.setText(rs.getString("nomecli")); // Preenche o nome do cliente
            
            // Preencher a situação e o tipo de OS
            cboOsSt.setSelectedItem(rs.getString("situacao")); // Supondo que cboOsSt é o JComboBox para situação
            if (rs.getString("tipo").equals("Orçamento")) {
                rbtOrc.setSelected(true); // Se o tipo for orçamento
            } else {
                rbtOs.setSelected(true); // Se for serviço
            }

        } else {
            JOptionPane.showMessageDialog(null, "OS não encontrada.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao pesquisar OS: " + e.getMessage());
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
        } catch (SQLException ex) {
            // Tratamento de exceção
        }
    }
}

   private void alterar_os() {
    String sql = "UPDATE tbos SET tipo=?, situacao=?, equipamento=?, defeito=?, servico=?, tecnico=?, valor=? WHERE os=?";
    
    try {
        pst = conexao.prepareStatement(sql);
        // Setando os valores dos parâmetros
        pst.setString(1, tipo); // Define o tipo, você deve definir o que é 'tipo'
        pst.setString(2, cboOsSt.getSelectedItem().toString());
        pst.setString(3, txtOsEquip.getText());
        pst.setString(4, txtOsDef.getText());
        pst.setString(5, txtOsServi.getText());
        pst.setString(6, txtOsTec.getText());
        pst.setString(7, txtOsValor.getText().replace(",", "."));
        pst.setString(8, txtOs.getText()); // O número da OS a ser alterada
        
        if (txtOs.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Preencha o número da OS para alterar!");
        } else {
            int alterado = pst.executeUpdate();
            if (alterado > 0) {
                JOptionPane.showMessageDialog(null, "OS alterada com sucesso!");
                // Limpa os campos após a atualização
                txtOs.setText(null);
                txtCliId.setText(null);
                txtOsEquip.setText(null);
                txtOsDef.setText(null);
                txtOsServi.setText(null);
                txtOsTec.setText(null);
                txtOsValor.setText(null);
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao alterar OS. Verifique se o número da OS está correto.");
            }
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e);
        e.printStackTrace();
    }
}

private void excluir_os() {
    int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir esta OS?", "Atenção", JOptionPane.YES_NO_OPTION);
    if (confirma == JOptionPane.YES_OPTION) {
        String sql = "DELETE FROM tbos WHERE os = ?";
        
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtOs.getText());
            
            if (pst.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "OS excluída com sucesso!");
                // Limpa os campos após exclusão
                txtOs.setText(null);
                txtData.setText(null);
                txtOsEquip.setText(null);
                txtOsDef.setText(null);
                txtOsServi.setText(null);
                txtOsTec.setText(null);
                txtOsValor.setText(null);
                txtCliId.setText(null);
                txtCliPesquisar.setText(null);
            } else {
                JOptionPane.showMessageDialog(null, "OS não encontrada.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir OS: " + e.getMessage());
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException ex) {
                // Tratamento de exceção
            }
        }
    }
}

 public void imprimirOs() {
    String numeroOs = JOptionPane.showInputDialog("Digite o número da OS que deseja imprimir:");
    if (numeroOs == null || numeroOs.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Número da OS não informado!");
        return;
    }

    String tipo = "", situacao = "", equipamento = "", defeito = "", servico = "", tecnico = "", valor = "", nomeCliente = "";

    String sql = "SELECT tbos.*, tbclientes.nomecli FROM tbos INNER JOIN tbclientes ON tbos.idcli = tbclientes.idcli WHERE os = ?";
    try {
        pst = conexao.prepareStatement(sql);
        pst.setString(1, numeroOs);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            tipo = rs.getString("tipo");
            situacao = rs.getString("situacao");
            equipamento = rs.getString("equipamento");
            defeito = rs.getString("defeito");
            servico = rs.getString("servico");
            tecnico = rs.getString("tecnico");
            valor = rs.getString("valor");
            nomeCliente = rs.getString("nomecli");  // Armazena o nome do cliente
        } else {
            JOptionPane.showMessageDialog(null, "OS não encontrada!");
            return;
        }

        String conteudo = "Ordem de Serviço\n\n" +
                          "Número da OS: " + numeroOs + "\n" +
                          "Nome do Cliente: " + nomeCliente + "\n" +  // Exibe o nome do cliente
                          "Tipo: " + tipo + "\n" +
                          "Situação: " + situacao + "\n" +
                          "Equipamento: " + equipamento + "\n" +
                          "Defeito: " + defeito + "\n" +
                          "Serviço: " + servico + "\n" +
                          "Técnico: " + tecnico + "\n" +
                          "Valor: " + valor + "\n\n" +
                          "_____________    _____________\n\n" +
                          "  VENDEDOR         CLIENTE\n\n" +
                          "-----------------------------------\n" +
                          "Data de Emissão: " + new java.util.Date() + "\n";

        try {
            FileWriter arquivo = new FileWriter("OS_" + numeroOs + ".txt");
            BufferedWriter writer = new BufferedWriter(arquivo);
            writer.write(conteudo);
            writer.close();

            JOptionPane.showMessageDialog(null, "Arquivo gerado com sucesso! Abra no Word para imprimir.");
            Runtime.getRuntime().exec("cmd /c start WINWORD OS_" + numeroOs + ".txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao gerar o arquivo: " + e.getMessage());
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar OS: " + e.getMessage());
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtOs = new javax.swing.JTextField();
        txtData = new javax.swing.JTextField();
        rbtOrc = new javax.swing.JRadioButton();
        rbtOs = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        cboOsSt = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        txtCliPesquisar = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtCliId = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        btnAdicionar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtOsEquip = new javax.swing.JTextField();
        txtOsDef = new javax.swing.JTextField();
        txtOsServi = new javax.swing.JTextField();
        txtOsTec = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtOsValor = new javax.swing.JTextField();
        btnOsAlterar = new javax.swing.JButton();
        btnOsAdicionar = new javax.swing.JButton();
        btnOsPesquisar = new javax.swing.JButton();
        btnOsExcluir = new javax.swing.JButton();
        btnOsImprimir = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setClosable(true);
        setIconifiable(true);
        setTitle("OS");
        setPreferredSize(new java.awt.Dimension(615, 440));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Nº OS");

        jLabel2.setText("Data");

        txtOs.setEditable(false);

        txtData.setEditable(false);
        txtData.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        buttonGroup1.add(rbtOrc);
        rbtOrc.setText("Orçamento");
        rbtOrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOrcActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbtOs);
        rbtOs.setText("Ordem de Serviço");
        rbtOs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txtOs, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(rbtOrc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rbtOs)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtOrc)
                    .addComponent(rbtOs))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jLabel3.setText("Situação");

        cboOsSt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Na bancada", "Entrega OK", "Orçamento REPROVADO", "Aguardando Aprovação", "Aguardando peças", "Abandonado pelo cliente", "Retornou" }));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente"));

        txtCliPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliPesquisarActionPerformed(evt);
            }
        });
        txtCliPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliPesquisarKeyReleased(evt);
            }
        });

        jLabel5.setText("🔍");

        jLabel6.setText("* Id");

        txtCliId.setEditable(false);

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Id", "Nome", "Fone"
            }
        ));
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblClientes);

        btnAdicionar.setText("+");
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCliId, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(txtCliId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdicionar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel7.setText("* Equipamento");

        jLabel8.setText("* Defeito");

        jLabel9.setText("Serviço");

        jLabel10.setText("Técnico");

        jLabel11.setText("valor Total");

        txtOsValor.setText("0");

        btnOsAlterar.setText("Alterar");
        btnOsAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsAlterarActionPerformed(evt);
            }
        });

        btnOsAdicionar.setText("Adicionar");
        btnOsAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsAdicionarActionPerformed(evt);
            }
        });

        btnOsPesquisar.setText("Pesquisar");
        btnOsPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsPesquisarActionPerformed(evt);
            }
        });

        btnOsExcluir.setText("Excluir");
        btnOsExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsExcluirActionPerformed(evt);
            }
        });

        btnOsImprimir.setText("Imprimir");
        btnOsImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsImprimirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboOsSt, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtOsServi)
                                    .addComponent(txtOsEquip)
                                    .addComponent(txtOsDef)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtOsTec, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(52, 52, 52)
                                                .addComponent(btnOsPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(12, 12, 12)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel11)
                                                .addGap(3, 3, 3)
                                                .addComponent(txtOsValor))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnOsAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnOsExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnOsImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 22, Short.MAX_VALUE))))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addComponent(btnOsAdicionar)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboOsSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtOsEquip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOsDef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOsServi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOsTec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(txtOsValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOsPesquisar)
                    .addComponent(btnOsAlterar)
                    .addComponent(btnOsAdicionar)
                    .addComponent(btnOsExcluir)
                    .addComponent(btnOsImprimir))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCliPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliPesquisarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCliPesquisarActionPerformed

    private void btnOsAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsAlterarActionPerformed
        // TODO add your handling code here:
                alterar_os();

    }//GEN-LAST:event_btnOsAlterarActionPerformed

    private void txtCliPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliPesquisarKeyReleased
        // TODO add your handling code here:
                pesquisarClientes();

        
    }//GEN-LAST:event_txtCliPesquisarKeyReleased

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        // TODO add your handling code here:
           int linhaSelecionada = tblClientes.getSelectedRow();
    
    if (linhaSelecionada != -1) { // Verifica se uma linha foi selecionada
        // Obtém o valor da primeira coluna (ID) da linha selecionada e define no campo txtCliId
        String idCliente = tblClientes.getModel().getValueAt(linhaSelecionada, 0).toString();
        txtCliId.setText(idCliente);
    }
        
        
        
        
    }//GEN-LAST:event_tblClientesMouseClicked

    private void rbtOrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOrcActionPerformed
        // TODO add your handling code here:
        
        tipo = "Orçamento";
        
    }//GEN-LAST:event_rbtOrcActionPerformed

    private void rbtOsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOsActionPerformed
        // TODO add your handling code here:
                
        tipo = "OS";

    }//GEN-LAST:event_rbtOsActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        
        rbtOrc.setSelected(true);
        tipo="Orçamento";
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnOsAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsAdicionarActionPerformed
        // TODO add your handling code here:
        emitir_os();
        
        
    }//GEN-LAST:event_btnOsAdicionarActionPerformed

    private void btnOsPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsPesquisarActionPerformed
        // TODO add your handling code here:
        
        pesquisar_os();
        
        
        
    }//GEN-LAST:event_btnOsPesquisarActionPerformed

    private void btnOsExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsExcluirActionPerformed
        // TODO add your handling code here:
        excluir_os();
    }//GEN-LAST:event_btnOsExcluirActionPerformed

    private void btnOsImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsImprimirActionPerformed
        // TODO add your handling code here:
        
        imprimirOs();

    }//GEN-LAST:event_btnOsImprimirActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        // TODO add your handling code here:
    TelaCliente telaCliente = new TelaCliente();
    
    // Adiciona a tela no desktop (caso esteja utilizando JDesktopPane)
    getDesktopPane().add(telaCliente);
    
    // Torna a tela visível
    telaCliente.setVisible(true);
        
        
        
    }//GEN-LAST:event_btnAdicionarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnOsAdicionar;
    private javax.swing.JButton btnOsAlterar;
    private javax.swing.JButton btnOsExcluir;
    private javax.swing.JButton btnOsImprimir;
    private javax.swing.JButton btnOsPesquisar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboOsSt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JRadioButton rbtOrc;
    private javax.swing.JRadioButton rbtOs;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtCliId;
    private javax.swing.JTextField txtCliPesquisar;
    private javax.swing.JTextField txtData;
    private javax.swing.JTextField txtOs;
    private javax.swing.JTextField txtOsDef;
    private javax.swing.JTextField txtOsEquip;
    private javax.swing.JTextField txtOsServi;
    private javax.swing.JTextField txtOsTec;
    private javax.swing.JTextField txtOsValor;
    // End of variables declaration//GEN-END:variables
}
