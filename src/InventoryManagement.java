
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InventoryManagement extends JFrame {
   // Database Connectivity Configuration Parameters
   private static final String URL = "jdbc:mysql://localhost:3306/InventoryDB?allowMultiQueries=true";
   private static final String USER = "[user]";
   private static final String PASS = "[password]";

   // UI Component Tracking Handles
   private JTextField txtID, txtName, txtCat, txtSup, txtWare, txtPrice, txtStock, txtSearch;
   private JTable table;
   private DefaultTableModel model;

   public InventoryManagement() {
       // Main Desktop Window Settings
       setTitle("Enterprise Inventory Management System");
       setSize(1200, 650);
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       setLocationRelativeTo(null);
       setLayout(new BorderLayout(10, 10));

       // MENU BAR SETUP
       JMenuBar menuBar = new JMenuBar();
       JMenu menu = new JMenu("Options");
       JMenuItem helpItem = new JMenuItem("Help");
       JMenuItem exitItem = new JMenuItem("Exit");
       menu.add(helpItem);
       menu.add(exitItem);
       menuBar.add(menu);
       setJMenuBar(menuBar);

       // TOP PANEL - PRODUCT ATTRIBUTES SUBFORM
       JPanel panelTop = new JPanel(new GridLayout(4, 4, 10, 10));
       panelTop.setBorder(BorderFactory.createTitledBorder("Product Attribute Entry Console"));
       txtID = new JTextField();
       txtName = new JTextField();
       txtCat = new JTextField();
       txtSup = new JTextField();
       txtWare = new JTextField();
       txtPrice = new JTextField();
       txtStock = new JTextField();

       panelTop.add(new JLabel("Product ID (for Update/Delete):")); panelTop.add(txtID);
       panelTop.add(new JLabel("Product Name:")); panelTop.add(txtName);
       panelTop.add(new JLabel("Category ID:")); panelTop.add(txtCat);
       panelTop.add(new JLabel("Supplier ID:")); panelTop.add(txtSup);
       panelTop.add(new JLabel("Warehouse ID:")); panelTop.add(txtWare);
       panelTop.add(new JLabel("Unit Price (₹):")); panelTop.add(txtPrice);
       panelTop.add(new JLabel("Initial Stock Count:")); panelTop.add(txtStock);
       add(panelTop, BorderLayout.NORTH);

       // CENTER PANEL - MATERIAL DATA MONITOR
       model = new DefaultTableModel(
               new String[]{"Product ID", "Product Name", "Category ID", "Supplier ID", "Warehouse ID", "Price (₹)", "Stock Count"}, 0) {
           @Override
           public boolean isCellEditable(int row, int column) { return false; } // Immutable grid mapping cells
       };
       table = new JTable(model);
       add(new JScrollPane(table), BorderLayout.CENTER);

       // BOTTOM PANEL - SYSTEM RUNTIME EVENT DASHBOARD
       JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
       JButton btnAdd = new JButton("Add Product");
       JButton btnView = new JButton("View All Products");
       JButton btnUpdate = new JButton("Update Stock Level");
       JButton btnDelete = new JButton("Purge Product Record");
       JButton btnSearch = new JButton("Search Matrix");
       JButton btnClear = new JButton("Reset Fields");
       JButton btnRefresh = new JButton("Refresh Frame");
       JButton btnExit = new JButton("Terminate");
       
       JButton btnAddViaProc = new JButton("Execute Stored Proc");
       JButton btnCheckTotalValue = new JButton("Compute Aggregated Value");
       JButton btnViewLog = new JButton("View Audit Logs");
       JButton btnJoinQuery = new JButton("Execute Multi-Join");
       JButton btnSubQuery = new JButton("Run Advanced Subquery");
       JButton btnViewQuery = new JButton("Fetch Material View");
       JButton btnIndexQuery = new JButton("Inject B-Tree Index");

       txtSearch = new JTextField(15);
       panelBottom.add(new JLabel("Filter String Lookup:"));
       panelBottom.add(txtSearch);
       panelBottom.add(btnSearch);
       panelBottom.add(btnAdd);
       panelBottom.add(btnView);
       panelBottom.add(btnUpdate);
       panelBottom.add(btnDelete);
       panelBottom.add(btnClear);
       panelBottom.add(btnRefresh);
       panelBottom.add(btnExit);
       panelBottom.add(btnAddViaProc);
       panelBottom.add(btnCheckTotalValue);
       panelBottom.add(btnViewLog);
       panelBottom.add(btnJoinQuery);
       panelBottom.add(btnSubQuery);
       panelBottom.add(btnViewQuery);
       panelBottom.add(btnIndexQuery);
       add(panelBottom, BorderLayout.SOUTH);

       // BUSINESS EVENT ASSIGNMENTS
       btnAdd.addActionListener(e -> addProduct());
       btnAddViaProc.addActionListener(e -> addProductViaProcedure());
       btnView.addActionListener(e -> loadProducts());
       btnUpdate.addActionListener(e -> updateStock());
       btnDelete.addActionListener(e -> deleteProduct());
       btnSearch.addActionListener(e -> searchProduct());
       btnClear.addActionListener(e -> clearFields());
       btnRefresh.addActionListener(e -> loadProducts());
       btnExit.addActionListener(e -> System.exit(0));
       btnCheckTotalValue.addActionListener(e -> checkTotalValue());
       btnViewLog.addActionListener(e -> viewStockLog());
       btnJoinQuery.addActionListener(e -> showJoinQuery());
       btnSubQuery.addActionListener(e -> showSubquery());
       btnViewQuery.addActionListener(e -> loadProductSummaryView());
       btnIndexQuery.addActionListener(e -> createIndex());
       helpItem.addActionListener(e -> showHelp());
       exitItem.addActionListener(e -> System.exit(0));
       
       // Preload database snapshot on screen bootstrap
       loadProducts();
   }

   private Connection getConnection() throws SQLException {
       try { Class.forName("com.mysql.cj.jdbc.Driver"); }
       catch (ClassNotFoundException e) { throw new SQLException("MySQL Connection Driver Mapping Missing."); }
       return DriverManager.getConnection(URL, USER, PASS);
   }

   // STANDARD WRITE ROUTINE (Parameterized Transaction Entry)
   private void addProduct() {
       if (txtName.getText().isEmpty() || txtCat.getText().isEmpty() || txtSup.getText().isEmpty()
               || txtWare.getText().isEmpty() || txtPrice.getText().isEmpty() || txtStock.getText().isEmpty()) {
           JOptionPane.showMessageDialog(this, "Validation Failed: All attribute fields are mandatory.");
           return;
       }
       String sql = "INSERT INTO product (product_name, category_id, supplier_id, warehouse_id, price, stock) VALUES (?,?,?,?,?,?)";
       try (Connection conn = getConnection(); 
            PreparedStatement ps = conn.prepareStatement(sql)) {
           ps.setString(1, txtName.getText());
           ps.setInt(2, Integer.parseInt(txtCat.getText()));
           ps.setInt(3, Integer.parseInt(txtSup.getText()));
           ps.setInt(4, Integer.parseInt(txtWare.getText()));
           ps.setDouble(5, Double.parseDouble(txtPrice.getText()));
           ps.setInt(6, Integer.parseInt(txtStock.getText()));
           ps.executeUpdate();
           JOptionPane.showMessageDialog(this, "Transactional Registry Completed: Product mapped successfully.");
           loadProducts();
           clearFields();
       } catch (Exception e) { JOptionPane.showMessageDialog(this, "SQL Error Trace: " + e.getMessage()); }
   }

   // EXTENDED PL/SQL INTERACTION: CALLING STORED PROCEDURES
   private void addProductViaProcedure() {
       if (txtName.getText().isEmpty() || txtCat.getText().isEmpty() || txtSup.getText().isEmpty()
               || txtWare.getText().isEmpty() || txtPrice.getText().isEmpty() || txtStock.getText().isEmpty()) {
           JOptionPane.showMessageDialog(this, "Validation Failed: Enter structural fields to dispatch to the procedure.");
           return;
       }
       try (Connection conn = getConnection();
            CallableStatement cs = conn.prepareCall("{CALL AddNewProduct(?, ?, ?, ?, ?, ?)}")) {
           cs.setString(1, txtName.getText());
           cs.setInt(2, Integer.parseInt(txtCat.getText()));
           cs.setInt(3, Integer.parseInt(txtSup.getText()));
           cs.setInt(4, Integer.parseInt(txtWare.getText()));
           cs.setDouble(5, Double.parseDouble(txtPrice.getText()));
           cs.setInt(6, Integer.parseInt(txtStock.getText()));
           cs.execute();
           JOptionPane.showMessageDialog(this, "Database Procedure Dispatched: Stored Routine updated state successfully.");
           loadProducts();
           clearFields();
       } catch (Exception e) { JOptionPane.showMessageDialog(this, "Routine Execution Fault: " + e.getMessage()); }
   }

   // METRIC AUDIT PROCESSING: CALLING DATABASE USER-DEFINED FUNCTIONS
   private void checkTotalValue() {
       try (Connection conn = getConnection();
            CallableStatement cs = conn.prepareCall("{? = CALL GetTotalStockValue()}")) {
           cs.registerOutParameter(1, Types.DECIMAL);
           cs.execute();
           double cumulativeValue = cs.getDouble(1);
           JOptionPane.showMessageDialog(this, "Aggregated Valuation Check:\nTotal Inventory Net Asset Asset Value: ₹" + String.format("%.2f", cumulativeValue));
       } catch (Exception e) { JOptionPane.showMessageDialog(this, "Functional Metric Exception: " + e.getMessage()); }
   }

   // REFRESH GRID: FULL RELATIONAL ACQUISITION
   private void loadProducts() {
       model.setRowCount(0);
       // Re-establishing appropriate dynamic metadata matrix header frames matching original view configurations
       model.setColumnIdentifiers(new String[]{"Product ID", "Product Name", "Category ID", "Supplier ID", "Warehouse ID", "Price (₹)", "Stock Count"});
       String sql = "SELECT * FROM product ORDER BY product_id";
       try (Connection conn = getConnection(); 
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {
           while (rs.next()) {
               model.addRow(new Object[]{
                rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("category_id"),
rs.getInt("supplier_id"), rs.getInt("warehouse_id"), rs.getDouble("price"), rs.getInt("stock")
});
}
} catch (Exception e) { JOptionPane.showMessageDialog(this, "Acquisition Fault: " + e.getMessage()); }
}
// UPDATE CONCURRENT INVENTORY STATE (Triggers stock tracking data audit layers)
private void updateStock() {
if (txtID.getText().isEmpty() || txtStock.getText().isEmpty()) {
JOptionPane.showMessageDialog(this, "Enter Target Product ID and Updated Stock Count values.");
return;
}
String sql = "UPDATE product SET stock=? WHERE product_id=?";
try (Connection conn = getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {
ps.setInt(1, Integer.parseInt(txtStock.getText()));
ps.setInt(2, Integer.parseInt(txtID.getText()));
int affectedRows = ps.executeUpdate();
if (affectedRows > 0) {
JOptionPane.showMessageDialog(this, "Transactional Status: State synchronized. System triggers updated the Stock Log frame.");
loadProducts();
clearFields();
} else {
JOptionPane.showMessageDialog(this, "Target Error: Provided product pointer does not exist.");
}
} catch (Exception e) { JOptionPane.showMessageDialog(this, "State Modification Error: " + e.getMessage()); }
}
// DATA PURGE PIPELINE
private void deleteProduct() {
if (txtID.getText().isEmpty()) {
JOptionPane.showMessageDialog(this, "Validation Failure: Product ID vector token is required for deletion tasks.");
return;
}
String sql = "DELETE FROM product WHERE product_id=?";
try (Connection conn = getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {
ps.setInt(1, Integer.parseInt(txtID.getText()));
int affectedRows = ps.executeUpdate();
if (affectedRows > 0) {
JOptionPane.showMessageDialog(this, "Operation Concluded: Item removed from database registry mapping rows.");
loadProducts();
clearFields();
} else {
JOptionPane.showMessageDialog(this, "Execution Fault: Pointer mismatch. Specified ID row vector not found.");
}
} catch (Exception e) { JOptionPane.showMessageDialog(this, "Integrity Conflict: Could not delete row item. Ensure no active references exist in transaction logs."); }
}
// NON-BLOCKING STRING LOOKUP PARSING
private void searchProduct() {
model.setRowCount(0);
model.setColumnIdentifiers(new String[]{"Product ID", "Product Name", "Category ID", "Supplier ID", "Warehouse ID", "Price (₹)", "Stock Count"});
String sql = "SELECT * FROM product WHERE product_name LIKE ?";
try (Connection conn = getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {
ps.setString(1, "%" + txtSearch.getText() + "%");
try (ResultSet rs = ps.executeQuery()) {
while (rs.next()) {
model.addRow(new Object[]{
rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("category_id"),
rs.getInt("supplier_id"), rs.getInt("warehouse_id"), rs.getDouble("price"), rs.getInt("stock")
});
}
}
} catch (Exception e) { JOptionPane.showMessageDialog(this, "Search Fault Architecture Exception: " + e.getMessage()); }
}
// SYSTEM AUDIT COMPONENT WINDOW (Displays database log histories populated via triggers)
private void viewStockLog() {
JFrame logFrame = new JFrame("System Transaction Audit Trail Monitor");
logFrame.setSize(850, 400);
logFrame.setLocationRelativeTo(this);
DefaultTableModel logModel = new DefaultTableModel(
new Object[]{"Audit Log ID", "Target Product ID", "Historical Balance", "Adjusted Balance Update", "System Timestamp Reference", "Executing Database Node"}, 0);
JTable logTable = new JTable(logModel);
String sql = "SELECT log_id, product_id, old_stock, new_stock, change_time, changed_by FROM Stock_Log ORDER BY change_time DESC";
try (Connection conn = getConnection();
Statement st = conn.createStatement();
ResultSet rs = st.executeQuery(sql)) {
while (rs.next()) {
logModel.addRow(new Object[]{
rs.getInt("log_id"),
rs.getInt("product_id"),
rs.getInt("old_stock"),
rs.getInt("new_stock"),
rs.getTimestamp("change_time"),
rs.getString("changed_by")
});
}
} catch (SQLException e) {
JOptionPane.showMessageDialog(this, "Log Exception Error Trace: " + e.getMessage());
}
logFrame.add(new JScrollPane(logTable));
logFrame.setVisible(true);
}
// HIGH-IMPACT ENTERPRISE LOOKUP LOGIC: COMPILING MULTI-TABLE INTERSECTIONS (JOINS)
private void showJoinQuery() {
model.setRowCount(0);
// Re-indexing frame structures to present clean textual data abstractions instead of numeric foreign database keys
model.setColumnIdentifiers(new String[]{"Product ID", "Item Designation", "Assigned Category Track", "Origin Supplier Network", "Unit Cost Tier (₹)"});
String sql = "SELECT p.product_id, p.product_name, c.category_name, s.supplier_name, p.price " +
"FROM product p " +
"JOIN category c ON p.category_id = c.category_id " +
"JOIN supplier s ON p.supplier_id = s.supplier_id ORDER BY p.product_id";
try (Connection conn = getConnection();
Statement st = conn.createStatement();
ResultSet rs = st.executeQuery(sql)) {
while (rs.next()) {
model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3),
rs.getString(4), rs.getDouble(5)});
}
JOptionPane.showMessageDialog(this, "Multi-Table Join Execution Complete.\nRelational models compiled successfully across Category, Supplier, and Catalog nodes.");
} catch (Exception e) { JOptionPane.showMessageDialog(this, "Join Query Execution Fault: " + e.getMessage()); }
}
// SCALABLE AGGREGATE PROCESSING LOGIC: ADVANCED NESTED SUBQUERIES
private void showSubquery() {
model.setRowCount(0);
model.setColumnIdentifiers(new String[]{"Product Name Vector Item", "Evaluated Dynamic Unit Price (₹)"});
String sql = "SELECT product_name, price FROM product WHERE price > (SELECT AVG(price) FROM product) ORDER BY price DESC";
try (Connection conn = getConnection();
Statement st = conn.createStatement();
ResultSet rs = st.executeQuery(sql)) {
while (rs.next()) {
model.addRow(new Object[]{rs.getString(1), rs.getDouble(2)});
}
JOptionPane.showMessageDialog(this, "Nested Architectural Subquery Executed.\nDisplaying entries exceeding calculated Average System Value Tiers.");
} catch (Exception e) { JOptionPane.showMessageDialog(this, "Subquery Evaluation Failure: " + e.getMessage()); }
}
// READING FROM VIRTUAL MATERIAL ARCHITECTURES (VIEWS)
private void loadProductSummaryView() {
model.setRowCount(0);
model.setColumnIdentifiers(new String[]{"Product ID Pointer", "Product Name", "Categorization Track", "Supplier Hub Label"});
String sql = "SELECT * FROM product_summary";
try (Connection conn = getConnection();
Statement st = conn.createStatement();
ResultSet rs = st.executeQuery(sql)) {
while (rs.next()) {
model.addRow(new Object[]{rs.getInt("product_id"), rs.getString("product_name"),
rs.getString("category_name"), rs.getString("supplier_name")});
}
JOptionPane.showMessageDialog(this, "View Abstraction Layer Materialized.\nData streamed directly from virtual table: 'product_summary'.");
} catch (Exception e) { JOptionPane.showMessageDialog(this, "Virtual Framework Read Fault: " + e.getMessage()); }
}
// RE-INDEXING EXECUTION SPEEDS (Demonstrating performance optimizations logs directly)
private void createIndex() {
String sql = "CREATE INDEX idx_product_name ON product(product_name)";
try (Connection conn = getConnection();
Statement st = conn.createStatement()) {
st.execute(sql);
JOptionPane.showMessageDialog(this, "B-Tree Optimization Complete:\nIndex structure 'idx_product_name' committed safely across row index points.");
} catch (SQLException e) {
// Handle the situation gracefully if the index structure has already been initialized in the database environment metrics
if (e.getErrorCode() == 1061 || e.getMessage().contains("Duplicate key name")) {
JOptionPane.showMessageDialog(this, "B-Tree Optimization Insights:\nStrategic index layer 'idx_product_name' already resides on columns. Skipping operation.", "Optimization Notice", JOptionPane.INFORMATION_MESSAGE);
} else {
JOptionPane.showMessageDialog(this, "Indexing Module Error: " + e.getMessage());
}
} catch (Exception e) {
JOptionPane.showMessageDialog(this, "System Error: " + e.getMessage());
}
}
private void clearFields() {
txtID.setText(""); txtName.setText(""); txtCat.setText(""); txtSup.setText("");
txtWare.setText(""); txtPrice.setText(""); txtStock.setText(""); txtSearch.setText("");
}
private void showHelp() {
JOptionPane.showMessageDialog(this,
"Enterprise Inventory Console Management Framework\n\n" +
"Operational Features Architecture:\n" +
"• Reactive UI Action Bindings managing CRUD transactions across relational nodes.\n" +
"• Decoupled PL/SQL invocations via Stored Procedures and User Defined Functions.\n" +
"• Automated system change capture driven by data integrity Triggers.\n" +
"• Explicit B-Tree table indices accelerating multi-table structural search queries.\n" +
"• Secure localized transactional boundaries keeping memory states completely thread-safe.");
}
public static void main(String[] args) {
EventQueue.invokeLater(() -> {
try {
// Map host OS visual design components parameters to Java window frames
UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
} catch (Exception ignored) {}
new InventoryManagement().setVisible(true);
});
}
}
