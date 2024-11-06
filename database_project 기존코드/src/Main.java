import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseConnection 클래스
 * MySQL 데이터베이스와의 연결을 설정하는 역할을 함
 */
class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/database_project";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

/**
 * Main 클래스
 * 직원 정보 조회, 검색, 삭제, 수정 및 추가 기능을 관리하는 메인 클래스
 */
public class Main extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> searchCategoryBox, groupByBox;
    private JButton searchButton, deleteButton, addButton, updateButton;
    private JLabel selectedEmployeesLabel;
    private JCheckBox nameBox, ssnBox, bdateBox, addressBox, sexBox, salaryBox, supervisorBox, departmentBox;

    public Main() {
        setTitle("Information Retrieval System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 테이블 모델 설정 및 첫 번째 컬럼에 체크박스 추가
        model = new DefaultTableModel(new Object[]{"선택", "이름", "주민번호", "생년월일", "주소", "성별", "급여", "상사", "부서"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class; // 첫 번째 컬럼을 체크박스로 설정
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // 체크박스 컬럼만 수정 가능
            }
        };
        table = new JTable(model);

        // 테이블의 체크박스 컬럼을 설정
        TableColumn selectColumn = table.getColumnModel().getColumn(0);
        selectColumn.setMaxWidth(50); // 체크박스 컬럼 너비 설정

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(10);
        searchCategoryBox = new JComboBox<>(new String[]{"전체", "부서", "성별", "급여", "평균 월급"});
        groupByBox = new JComboBox<>(new String[]{"그룹 없음", "성별", "부서", "상급자"});
        searchButton = new JButton("검색");
        deleteButton = new JButton("선택된 직원 삭제");
        addButton = new JButton("직원 추가");
        updateButton = new JButton("직원 수정");

        groupByBox.setEnabled(false);

        nameBox = new JCheckBox("이름", true);
        ssnBox = new JCheckBox("주민번호", true);
        bdateBox = new JCheckBox("생년월일", true);
        addressBox = new JCheckBox("주소", true);
        sexBox = new JCheckBox("성별", true);
        salaryBox = new JCheckBox("급여", true);
        supervisorBox = new JCheckBox("상사", true);
        departmentBox = new JCheckBox("부서", true);

        searchPanel.add(new JLabel("검색 범위:"));
        searchPanel.add(searchCategoryBox);
        searchPanel.add(new JLabel("그룹별 평균 월급:"));
        searchPanel.add(groupByBox);
        searchPanel.add(new JLabel("검색 필드:"));
        searchPanel.add(searchField);
        searchPanel.add(nameBox);
        searchPanel.add(ssnBox);
        searchPanel.add(bdateBox);
        searchPanel.add(addressBox);
        searchPanel.add(sexBox);
        searchPanel.add(salaryBox);
        searchPanel.add(supervisorBox);
        searchPanel.add(departmentBox);
        searchPanel.add(searchButton);
        searchPanel.add(deleteButton);
        searchPanel.add(addButton);
        searchPanel.add(updateButton);

        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        selectedEmployeesLabel = new JLabel("선택된 직원: ");
        add(selectedEmployeesLabel, BorderLayout.SOUTH);

        searchCategoryBox.addActionListener(e -> updateSearchFieldAndGroupBy());

        searchButton.addActionListener(e -> searchEmployees());
        deleteButton.addActionListener(e -> deleteEmployees());
        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());

        loadEmployeeData();
    }

    private void updateSearchFieldAndGroupBy() {
        String selectedCategory = (String) searchCategoryBox.getSelectedItem();
        searchField.setText("");
        switch (selectedCategory) {
            case "부서" -> {
                searchField.setEnabled(true);
                searchField.setToolTipText("부서명을 입력하세요 (예: Administration)");
                groupByBox.setEnabled(false);
                groupByBox.setSelectedIndex(0);
            }
            case "성별" -> {
                searchField.setEnabled(true);
                searchField.setToolTipText("성별을 입력하세요 (M 또는 F)");
                groupByBox.setEnabled(false);
                groupByBox.setSelectedIndex(0);
            }
            case "급여" -> {
                searchField.setEnabled(true);
                searchField.setToolTipText("최소 급여를 입력하세요 (예: 30000)");
                groupByBox.setEnabled(false);
                groupByBox.setSelectedIndex(0);
            }
            case "평균 월급" -> {
                searchField.setEnabled(false);
                searchField.setToolTipText(null);
                groupByBox.setEnabled(true);
            }
            default -> {
                searchField.setEnabled(false);
                searchField.setToolTipText(null);
                groupByBox.setEnabled(false);
                groupByBox.setSelectedIndex(0);
            }
        }
    }

    private void setTableColumns() {
        model.setColumnCount(0);
        model.addColumn("선택");
        if (nameBox.isSelected()) model.addColumn("이름");
        if (ssnBox.isSelected()) model.addColumn("주민번호");
        if (bdateBox.isSelected()) model.addColumn("생년월일");
        if (addressBox.isSelected()) model.addColumn("주소");
        if (sexBox.isSelected()) model.addColumn("성별");
        if (salaryBox.isSelected()) model.addColumn("급여");
        if (supervisorBox.isSelected()) model.addColumn("상사");
        if (departmentBox.isSelected()) model.addColumn("부서");
    }

    private void loadEmployeeData() {
        String query = """
            SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department
            FROM EMPLOYEE e
            JOIN DEPARTMENT d ON e.Dno = d.Dnumber;
            """;

        model.setRowCount(0);
        setTableColumns();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                row.add(false);  // 체크박스 기본값은 false로 설정
                if (nameBox.isSelected()) row.add(rs.getString("Name"));
                if (ssnBox.isSelected()) row.add(rs.getString("Ssn"));
                if (bdateBox.isSelected()) row.add(rs.getDate("Bdate"));
                if (addressBox.isSelected()) row.add(rs.getString("Address"));
                if (sexBox.isSelected()) row.add(rs.getString("Sex"));
                if (salaryBox.isSelected()) row.add(rs.getDouble("Salary"));
                if (supervisorBox.isSelected()) row.add(rs.getString("Super_ssn"));
                if (departmentBox.isSelected()) row.add(rs.getString("Department"));
                model.addRow(row.toArray());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 조건에 따라 직원 데이터를 검색하고 테이블에 표시하는 메서드
     */
    private void searchEmployees() {
        // 검색 조건에 따라 쿼리 작성
        String searchText = searchField.getText().trim();
        String category = searchCategoryBox.getSelectedItem().toString();
        String groupBy = groupByBox.getSelectedItem().toString();

        String query;
        if ("부서".equals(category)) {
            query = """
                SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department
                FROM EMPLOYEE e
                JOIN DEPARTMENT d ON e.Dno = d.Dnumber
                WHERE d.Dname = ?;
                """;
        } else if ("성별".equals(category)) {
            query = """
                SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department
                FROM EMPLOYEE e
                JOIN DEPARTMENT d ON e.Dno = d.Dnumber
                WHERE e.Sex = ?;
                """;
        } else if ("급여".equals(category)) {
            query = """
                SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department
                FROM EMPLOYEE e
                JOIN DEPARTMENT d ON e.Dno = d.Dnumber
                WHERE e.Salary >= ?;
                """;
        } else if ("평균 월급".equals(category)) {
            if ("성별".equals(groupBy)) {
                query = """
                    SELECT e.Sex, AVG(e.Salary) AS AvgSalary
                    FROM EMPLOYEE e
                    GROUP BY e.Sex;
                    """;
            } else if ("부서".equals(groupBy)) {
                query = """
                    SELECT d.Dname AS Department, AVG(e.Salary) AS AvgSalary
                    FROM EMPLOYEE e
                    JOIN DEPARTMENT d ON e.Dno = d.Dnumber
                    GROUP BY d.Dname;
                    """;
            } else if ("상급자".equals(groupBy)) {
                query = """
                    SELECT e.Super_ssn AS Supervisor, AVG(e.Salary) AS AvgSalary
                    FROM EMPLOYEE e
                    GROUP BY e.Super_ssn;
                    """;
            } else {
                JOptionPane.showMessageDialog(this, "그룹을 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            loadEmployeeData();
            return;
        }

        // 검색 결과를 테이블에 표시
        model.setRowCount(0);
        setTableColumns();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (!"평균 월급".equals(category) && !"전체".equals(category)) {
                if ("급여".equals(category)) {
                    pstmt.setDouble(1, Double.parseDouble(searchText));
                } else {
                    pstmt.setString(1, searchText);
                }
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                row.add(false);  // 체크박스 기본값은 false로 설정
                if ("평균 월급".equals(category)) {
                    if ("성별".equals(groupBy)) {
                        row.add(rs.getString("Sex"));
                        row.add(rs.getDouble("AvgSalary"));
                    } else if ("부서".equals(groupBy)) {
                        row.add(rs.getString("Department"));
                        row.add(rs.getDouble("AvgSalary"));
                    } else if ("상급자".equals(groupBy)) {
                        row.add(rs.getString("Supervisor"));
                        row.add(rs.getDouble("AvgSalary"));
                    }
                } else {
                    if (nameBox.isSelected()) row.add(rs.getString("Name"));
                    if (ssnBox.isSelected()) row.add(rs.getString("Ssn"));
                    if (bdateBox.isSelected()) row.add(rs.getDate("Bdate"));
                    if (addressBox.isSelected()) row.add(rs.getString("Address"));
                    if (sexBox.isSelected()) row.add(rs.getString("Sex"));
                    if (salaryBox.isSelected()) row.add(rs.getDouble("Salary"));
                    if (supervisorBox.isSelected()) row.add(rs.getString("Super_ssn"));
                    if (departmentBox.isSelected()) row.add(rs.getString("Department"));
                }
                model.addRow(row.toArray());
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "올바른 입력을 확인하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 체크된 직원들을 삭제하는 메서드
     */
    private void deleteEmployees() {
        List<String> ssnsToDelete = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, 0);
            if (isSelected) {
                String ssn = model.getValueAt(i, 2).toString(); // "주민번호"가 2번째 컬럼
                ssnsToDelete.add(ssn);
            }
        }

        if (ssnsToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "삭제할 직원을 선택하세요.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "선택한 직원들을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            String query = "DELETE FROM EMPLOYEE WHERE Ssn = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                for (String ssn : ssnsToDelete) {
                    pstmt.setString(1, ssn);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            conn.commit();
            loadEmployeeData();
            JOptionPane.showMessageDialog(this, "선택된 직원이 삭제되었습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 새 직원을 추가하는 메서드
     */
    private void addEmployee() {
        JTextField fNameField = new JTextField();
        JTextField mInitField = new JTextField();
        JTextField lNameField = new JTextField();
        JTextField ssnField = new JTextField();
        JTextField bDateField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField sexField = new JTextField();
        JTextField salaryField = new JTextField();
        JTextField superSsnField = new JTextField();
        JTextField dnoField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(10, 2));
        panel.add(new JLabel("이름:"));
        panel.add(fNameField);
        panel.add(new JLabel("중간 이니셜:"));
        panel.add(mInitField);
        panel.add(new JLabel("성:"));
        panel.add(lNameField);
        panel.add(new JLabel("주민번호:"));
        panel.add(ssnField);
        panel.add(new JLabel("생년월일 (YYYY-MM-DD):"));
        panel.add(bDateField);
        panel.add(new JLabel("주소:"));
        panel.add(addressField);
        panel.add(new JLabel("성별 (M/F):"));
        panel.add(sexField);
        panel.add(new JLabel("급여:"));
        panel.add(salaryField);
        panel.add(new JLabel("상사 주민번호:"));
        panel.add(superSsnField);
        panel.add(new JLabel("부서 번호:"));
        panel.add(dnoField);

        int result = JOptionPane.showConfirmDialog(this, panel, "새 직원 추가", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String query = """
                INSERT INTO EMPLOYEE (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno, created, modified)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
                """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, fNameField.getText());
                pstmt.setString(2, mInitField.getText());
                pstmt.setString(3, lNameField.getText());
                pstmt.setString(4, ssnField.getText());
                pstmt.setDate(5, Date.valueOf(bDateField.getText()));
                pstmt.setString(6, addressField.getText());
                pstmt.setString(7, sexField.getText());
                pstmt.setDouble(8, Double.parseDouble(salaryField.getText()));
                pstmt.setString(9, superSsnField.getText());
                pstmt.setInt(10, Integer.parseInt(dnoField.getText()));

                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "새 직원이 추가되었습니다.");
                    loadEmployeeData();
                }

            } catch (SQLException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "올바른 입력을 확인하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 선택된 직원의 정보를 수정하는 메서드
     */
    private void updateEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "수정할 직원을 선택하세요.");
            return;
        }

        JTextField fNameField = new JTextField(model.getValueAt(selectedRow, 1).toString());
        JTextField ssnField = new JTextField(model.getValueAt(selectedRow, 2).toString());
        JTextField bDateField = new JTextField(model.getValueAt(selectedRow, 3).toString());
        JTextField addressField = new JTextField(model.getValueAt(selectedRow, 4).toString());
        JTextField sexField = new JTextField(model.getValueAt(selectedRow, 5).toString());
        JTextField salaryField = new JTextField(model.getValueAt(selectedRow, 6).toString());
        JTextField superSsnField = new JTextField(model.getValueAt(selectedRow, 7).toString());
        JTextField dnoField = new JTextField(model.getValueAt(selectedRow, 8).toString());

        JPanel panel = new JPanel(new GridLayout(8, 2));
        panel.add(new JLabel("이름:"));
        panel.add(fNameField);
        panel.add(new JLabel("주민번호:"));
        panel.add(ssnField);
        panel.add(new JLabel("생년월일 (YYYY-MM-DD):"));
        panel.add(bDateField);
        panel.add(new JLabel("주소:"));
        panel.add(addressField);
        panel.add(new JLabel("성별 (M/F):"));
        panel.add(sexField);
        panel.add(new JLabel("급여:"));
        panel.add(salaryField);
        panel.add(new JLabel("상사 주민번호:"));
        panel.add(superSsnField);
        panel.add(new JLabel("부서 번호:"));
        panel.add(dnoField);

        int result = JOptionPane.showConfirmDialog(this, panel, "직원 수정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String query = """
                UPDATE EMPLOYEE SET Fname = ?, Ssn = ?, Bdate = ?, Address = ?, Sex = ?, Salary = ?, Super_ssn = ?, Dno = ?, modified = CURRENT_TIMESTAMP
                WHERE Ssn = ?;
                """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, fNameField.getText());
                pstmt.setString(2, ssnField.getText());
                pstmt.setDate(3, Date.valueOf(bDateField.getText()));
                pstmt.setString(4, addressField.getText());
                pstmt.setString(5, sexField.getText());
                pstmt.setDouble(6, Double.parseDouble(salaryField.getText()));
                pstmt.setString(7, superSsnField.getText());
                pstmt.setInt(8, Integer.parseInt(dnoField.getText()));
                pstmt.setString(9, ssnField.getText());

                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "직원 정보가 수정되었습니다.");
                    loadEmployeeData();
                }

            } catch (SQLException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "올바른 입력을 확인하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main management = new Main();
            management.setVisible(true);
        });
    }
}
