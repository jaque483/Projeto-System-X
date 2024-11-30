/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.infox.telas;

import java.sql.*;
import br.com.infox.dal.ModuloConexao;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author jaque
 */
public class TelaCliente extends javax.swing.JInternalFrame {
Connection conexao=null;
PreparedStatement pst=null;
ResultSet rs=null;
    /**
     * Creates new form TelaCliente
     */
    public TelaCliente() {
        initComponents();
        conexao=ModuloConexao.conector();
        
    }

    private void adicionar(){
        String sql = "insert into tbclientes(nomecli, endcli, fonecli, emailcli)values(?, ?, ?, ?)";
        try {
            pst=conexao.prepareStatement(sql);
            pst.setString(1, txtCliNome.getText());
            pst.setString(2, txtCliEndereco.getText());
            pst.setString(3,txtCliFone.getText());
            pst.setString(4, txtCliEmail.getText());
           
            if ((txtCliNome.getText().isEmpty()) || (txtCliFone.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
               
            
            
            int adicionado = pst.executeUpdate();
           // System.out.println(adicionado);
            if (adicionado >0){
                JOptionPane.showMessageDialog(null, "Cliente adicionado com sucesso");
                txtCliNome.setText(null);
                txtCliEndereco.setText(null);
                txtCliFone.setText(null);
                txtCliEmail.setText(null);
               
                
            }
            
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

            
        }
        
     
        
        
    }

    private void pesquisarCliente() {
    String sql = "SELECT * FROM tbclientes WHERE nomecli LIKE ?";
    try {
        pst = conexao.prepareStatement(sql);
        pst.setString(1, txtCliPesquisar.getText() + "%");
        rs = pst.executeQuery();
        
        DefaultTableModel modelo = (DefaultTableModel) tblClientes.getModel();
        modelo.setRowCount(0); // Limpa a tabela antes de adicionar novos dados
        
        if (!rs.next()) { // Verifica se há resultados
            JOptionPane.showMessageDialog(null, "Nenhum cliente encontrado!");
        } else {
            do {
                modelo.addRow(new Object[]{
                    rs.getString("idcli"),
                    rs.getString("nomecli"),
                    rs.getString("endcli"),
                    rs.getString("fonecli"),
                    rs.getString("emailcli")
                });
            } while (rs.next());
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e);
    } finally {
        try {
            if (rs != null) rs.close(); // Fechar o ResultSet
            if (pst != null) pst.close(); // Fechar o PreparedStatement
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
}

// Exemplo de como alterarCliente pode ser modificado
private void alterarCliente() {
    int selectedRow = tblClientes.getSelectedRow();  // Get the selected row index

    if (selectedRow >= 0) { // Check if a row is selected
        int idCliente = Integer.parseInt(tblClientes.getValueAt(selectedRow, 0).toString()); // Get the ID from the selected row
        String nomeAtual = tblClientes.getValueAt(selectedRow, 1).toString();
        String enderecoAtual = tblClientes.getValueAt(selectedRow, 2).toString();
        String telefoneAtual = tblClientes.getValueAt(selectedRow, 3).toString();
        String emailAtual = tblClientes.getValueAt(selectedRow, 4).toString();

        // Build the UPDATE query dynamically based on modified fields
        StringBuilder sql = new StringBuilder("UPDATE tbclientes SET ");
        boolean hasChanged = false; // Track if any field has been modified

        if (!txtCliNome.getText().isEmpty() && !txtCliNome.getText().equals(nomeAtual)) {
            sql.append("nomecli=?, ");
            hasChanged = true;
        }
        if (!txtCliEndereco.getText().isEmpty() && !txtCliEndereco.getText().equals(enderecoAtual)) {
            sql.append("endcli=?, ");
            hasChanged = true;
        }
        if (!txtCliFone.getText().isEmpty() && !txtCliFone.getText().equals(telefoneAtual)) {
            sql.append("fonecli=?, ");
            hasChanged = true;
        }
        if (!txtCliEmail.getText().isEmpty() && !txtCliEmail.getText().equals(emailAtual)) {
            sql.append("emailcli=?, ");
            hasChanged = true;
        }

        if (hasChanged) {
            sql.delete(sql.length() - 2, sql.length()); // Remove trailing comma and space
            sql.append(" WHERE idcli=?");

            try {
                pst = conexao.prepareStatement(sql.toString());
                int index = 1;  // Index for setting PreparedStatement parameters

                if (hasChanged) {
                    if (!txtCliNome.getText().isEmpty() && !txtCliNome.getText().equals(nomeAtual)) {
                        pst.setString(index++, txtCliNome.getText());
                    }
                    if (!txtCliEndereco.getText().isEmpty() && !txtCliEndereco.getText().equals(enderecoAtual)) {
                        pst.setString(index++, txtCliEndereco.getText());
                    }
                    if (!txtCliFone.getText().isEmpty() && !txtCliFone.getText().equals(telefoneAtual)) {
                        pst.setString(index++, txtCliFone.getText());
                    }
                    if (!txtCliEmail.getText().isEmpty() && !txtCliEmail.getText().equals(emailAtual)) {
                        pst.setString(index++, txtCliEmail.getText());
                    }
                }

                pst.setInt(index, idCliente); // Set the ID parameter last

                if (pst.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(null, "Dados do cliente atualizados com sucesso!");
                    // Clear input fields after successful update
                    txtCliNome.setText(null);
                    txtCliEndereco.setText(null);
                    txtCliFone.setText(null);
                    txtCliEmail.setText(null);
                    // Consider refreshing the table to reflect changes
                    pesquisarCliente(); // Optional: Refresh table data
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            } finally {
                try {
                    if (pst != null) pst.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhuma alteração detectada.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Selecione um cliente para alterar!");
    }
}


// Exemplo de como removerCliente pode ser modificado
private void removerCliente() {
    int linha = tblClientes.getSelectedRow(); // Pega a linha selecionada
    if (linha >= 0) { // Verifica se uma linha está selecionada
        int idCliente = Integer.parseInt(tblClientes.getValueAt(linha, 0).toString()); // ID do cliente
        
        // Verificar se o cliente está vinculado a alguma OS
        String sqlVerificaOS = "SELECT COUNT(*) FROM tbos WHERE idcli = ?";
        try {
            pst = conexao.prepareStatement(sqlVerificaOS);
            pst.setInt(1, idCliente);
            rs = pst.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // Se o cliente estiver vinculado a alguma OS, não permite a exclusão
                JOptionPane.showMessageDialog(null, "Não é possível remover o cliente. Este cliente possui ordens de serviço vinculadas.");
                return; // Impede a exclusão
            }

            // Se não estiver vinculado a nenhuma OS, prossegue com a exclusão
            int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este cliente?", "Atenção", JOptionPane.YES_NO_OPTION);
            if (confirma == JOptionPane.YES_OPTION) {
                String sqlExcluir = "DELETE FROM tbclientes WHERE idcli=?";
                pst = conexao.prepareStatement(sqlExcluir);
                pst.setInt(1, idCliente); // Adiciona o ID do cliente ao PreparedStatement

                if (pst.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(null, "Cliente removido com sucesso!");
                    // Limpa os campos após a remoção
                    txtCliNome.setText(null);
                    txtCliEndereco.setText(null);
                    txtCliFone.setText(null);
                    txtCliEmail.setText(null);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao verificar vínculo com OS: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao fechar recursos: " + ex.getMessage());
            }
        }
    } else {
        JOptionPane.showMessageDialog(null, "Selecione um cliente para remover!");
    }
}




private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {
     int selectedRow = tblClientes.getSelectedRow();
    if (selectedRow >= 0) {
        try {
            // Get the values from the selected row
            int idCliente = Integer.parseInt(tblClientes.getValueAt(selectedRow, 0).toString());
            String nome = tblClientes.getValueAt(selectedRow, 1).toString();
            String endereco = tblClientes.getValueAt(selectedRow, 2).toString();
            String telefone = tblClientes.getValueAt(selectedRow, 3).toString();
            String email = tblClientes.getValueAt(selectedRow, 4).toString();

            // Populate the text fields
            txtCliNome.setText(nome);
            txtCliEndereco.setText(endereco);
            txtCliFone.setText(telefone);
            txtCliEmail.setText(email);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Erro ao converter o ID para número inteiro.");
        }
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCliNome = new javax.swing.JTextField();
        txtCliEndereco = new javax.swing.JTextField();
        txtCliEmail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCliPesquisar = new javax.swing.JTextField();
        btnPesquisar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        btnAdicionar = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        txtCliFone = new javax.swing.JFormattedTextField();

        setClosable(true);
        setIconifiable(true);
        setTitle("Clientes");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jLabel1.setText("Nome * ");

        jLabel2.setText("Endereço");

        jLabel3.setText("Telefone * ");

        jLabel4.setText("E-mail");

        jLabel5.setText("* Campos obrigatórios");

        btnPesquisar.setText("Pesquisar");
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Endereço", "Telefone", "E-mail"
            }
        ));
        tblClientes.setFocusable(false);
        tblClientes.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblClientes);

        btnAdicionar.setText("Adicionar ");
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnAlterar.setText("Alterar");
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        btnRemover.setText("Remover");
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        try {
            txtCliFone.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)#####-#####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(btnPesquisar)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(157, 157, 157)
                            .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                            .addComponent(btnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addComponent(jLabel2)
                                .addComponent(jLabel4)
                                .addComponent(jLabel1))
                            .addGap(47, 47, 47)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtCliEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                                .addComponent(txtCliEndereco, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                                .addComponent(txtCliNome, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                                .addComponent(txtCliFone)))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCliNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCliEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAdicionar)
                            .addComponent(btnAlterar)
                            .addComponent(btnRemover))
                        .addGap(34, 34, 34))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCliFone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        // TODO add your handling code here:
        adicionar();
        
        
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        // TODO add your handling code here:
        pesquisarCliente();

    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        // TODO add your handling code here:
        alterarCliente();

        
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        // TODO add your handling code here:
        removerCliente();

    }//GEN-LAST:event_btnRemoverActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    txtCliNome.setText(null);
    txtCliEndereco.setText(null);
    txtCliFone.setText(null);
    txtCliEmail.setText(null);
    
    // Deselect any selected row in the table
    tblClientes.clearSelection();
        
    }//GEN-LAST:event_formMouseClicked

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnRemover;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtCliEmail;
    private javax.swing.JTextField txtCliEndereco;
    private javax.swing.JFormattedTextField txtCliFone;
    private javax.swing.JTextField txtCliNome;
    private javax.swing.JTextField txtCliPesquisar;
    // End of variables declaration//GEN-END:variables
}
