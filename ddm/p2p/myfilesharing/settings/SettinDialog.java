/*
 * SettinDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package ddm.p2p.myfilesharing.settings;

import javax.swing.JFileChooser;

import ddm.p2p.myfilesharing.utils.AddressUtil;
import ddm.p2p.myfilesharing.utils.PropertiesUtil;
import ddm.p2p.myfilesharing.view.P2PForm;

/**
 *
 * @author  __USER__
 */
public class SettinDialog extends javax.swing.JDialog {

	/** Creates new form SettinDialog */
	public SettinDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel1 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		browseSharedPath = new javax.swing.JButton();
		sharePathField = new javax.swing.JTextField();
		jPanel2 = new javax.swing.JPanel();
		jTextField1 = new javax.swing.JTextField();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("\u8bbe\u7f6e");

		jPanel3.setBorder(javax.swing.BorderFactory
				.createTitledBorder("\u9ed8\u8ba4\u4fdd\u5b58\u76ee\u5f55"));

		browseSharedPath.setText("\u6d4f\u89c8");
		browseSharedPath.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				browseSharedPathActionPerformed(evt);
			}
		});

		sharePathField.setText(P2PForm.sharedpath);

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				jPanel3Layout.createSequentialGroup().addContainerGap()
						.addComponent(sharePathField,
								javax.swing.GroupLayout.DEFAULT_SIZE, 283,
								Short.MAX_VALUE).addGap(10, 10, 10)
						.addComponent(browseSharedPath).addContainerGap()));
		jPanel3Layout
				.setVerticalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																browseSharedPath)
														.addComponent(
																sharePathField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout.createSequentialGroup().addContainerGap()
						.addComponent(jPanel3,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout.createSequentialGroup().addContainerGap()
						.addComponent(jPanel3,
								javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(188, Short.MAX_VALUE)));

		jTabbedPane1
				.addTab(
						"\u5171\u4eab",
						new javax.swing.ImageIcon(
								"C:\\Documents and Settings\\Administrator\\Workspaces\\MyEclipse 8.5\\DDM\\icons\\设置tab.png"),
						jPanel1); // NOI18N

		jTextField1.setText(AddressUtil.getUserName());
		jTextField1.setBorder(javax.swing.BorderFactory
				.createTitledBorder("\u6635\u79f0"));

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel2Layout.createSequentialGroup().addContainerGap()
						.addComponent(jTextField1,
								javax.swing.GroupLayout.PREFERRED_SIZE, 262,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(134, Short.MAX_VALUE)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel2Layout.createSequentialGroup().addContainerGap()
						.addComponent(jTextField1,
								javax.swing.GroupLayout.PREFERRED_SIZE, 56,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(194, Short.MAX_VALUE)));

		jTabbedPane1
				.addTab(
						"\u6635\u79f0",
						new javax.swing.ImageIcon(
								"C:\\Documents and Settings\\Administrator\\Workspaces\\MyEclipse 8.5\\DDM\\icons\\设置昵称.png"),
						jPanel2); // NOI18N

		jButton1.setText("\u786e\u5b9a");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jButton2.setText("\u53d6\u6d88");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																jTabbedPane1,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																411,
																Short.MAX_VALUE)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jButton1)
																		.addGap(
																				18,
																				18,
																				18)
																		.addComponent(
																				jButton2)))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												jTabbedPane1,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												298, Short.MAX_VALUE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jButton2)
														.addComponent(jButton1))
										.addContainerGap()));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		PropertiesUtil rc = new PropertiesUtil();
		rc.setValue("sharedpath", this.sharePathField.getText());
		rc.setValue("username", this.jTextField1.getText());
		rc.saveFile("config.properties", "sysconfig");
		this.dispose();
	}

	private void browseSharedPathActionPerformed(java.awt.event.ActionEvent evt) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle("选择共享文件夹");
		int ret = fileChooser.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			this.sharePathField.setText(fileChooser.getSelectedFile()
					.getAbsolutePath());
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				SettinDialog dialog = new SettinDialog(
						new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton browseSharedPath;
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField sharePathField;
	// End of variables declaration//GEN-END:variables

}