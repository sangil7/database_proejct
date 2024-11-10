import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DatabaseConnection 클래스
 * MySQL 데이터베이스와의 연결을 설정하는 역할을 함
 */
class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/databasebasic";
    private static final String USER = "root";
    private static final String PASSWORD = "jjeongee1234";

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
    private JComboBox<String> searchCategoryBox, groupByBox, departmentComboBox, genderComboBox, sortByBox;
    private JTextField salaryField;
    private JButton searchButton, deleteButton, addButton, updateButton;
    private JCheckBox nameBox, ssnBox, bdateBox, addressBox, sexBox, salaryBox, supervisorBox, departmentBox;

    private JTable projectTable;
    private DefaultTableModel projectModel;
    private JComboBox<String> projectSearchCategoryBox;
    private JTextField projectNameField;
    private JButton projectSearchButton, projectAddButton, projectUpdateButton, projectDeleteButton;

    //yong
    private void toggleSortOrder(TableRowSorter<TableModel> sorter, int column) {
        if (sorter.getSortKeys().isEmpty() || sorter.getSortKeys().get(0).getColumn() != column) {
            sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(column, SortOrder.ASCENDING)));
        } else {
            RowSorter.SortKey sortKey = sorter.getSortKeys().get(0);
            SortOrder order = sortKey.getSortOrder() == SortOrder.ASCENDING ? SortOrder.DESCENDING : SortOrder.ASCENDING;
            sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(column, order)));
        }
    }
    //~yong

    public Main() {
        setTitle("Information Retrieval System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 테이블 모델 설정 및 첫 번째 컬럼에 체크박스 추가
        model = new DefaultTableModel(new Object[]{"선택", "이름", "주민번호", "생년월일", "주소", "성별", "급여", "상사", "부서", "총 작업시간"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class; // 첫 번째 컬럼을 체크박스로 설정
                if (columnIndex == 9) return Double.class; // 총 작업 시간
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // 체크박스 컬럼만 수정 가능
            }
        };

        table = new JTable(model);

        //yong
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());

                toggleSortOrder(sorter, column);
            }
        });
        //~yong

        projectModel = new DefaultTableModel(new Object[]{"프로젝트 이름", "프로젝트 번호", "프로젝트 위치", "부서 번호"}, 0) {
            @Override
            public Class<?> getColumnClass(int projectColumnIndex) {
                if (projectColumnIndex == 0) return Boolean.class; // 첫 번째 컬럼을 체크박스로 설정
                return super.getColumnClass(projectColumnIndex);
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // 체크박스 컬럼만 수정 가능
            }
        };

        // 검색 조건 패널 설정
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchCategoryBox = new JComboBox<>(new String[]{"전체", "부서", "성별", "급여", "주소", "상사 주민번호", "생년월일", "이름", "주민번호", "평균 월급"});
        groupByBox = new JComboBox<>(new String[]{"그룹 없음", "성별", "부서", "상급자"});
        departmentComboBox = new JComboBox<>(getDepartments()); // 부서 선택을 위한 콤보박스
        genderComboBox = new JComboBox<>(new String[]{"M", "F"}); // 성별 선택을 위한 콤보박스
//        sortByBox = new JComboBox<>(new String[]{"기본 정렬", "총 작업시간 정렬"}); // 정렬 기준 추가
        salaryField = new JTextField(10);
        salaryField.setEnabled(false); // 검색 조건 필드는 기본적으로 비활성화

        searchButton = new JButton("검색");
        deleteButton = new JButton("선택된 직원 삭제");
        addButton = new JButton("직원 추가");
        updateButton = new JButton("직원 수정");

        searchPanel.add(new JLabel("검색 범위:"));
        searchPanel.add(searchCategoryBox);
        searchPanel.add(new JLabel("그룹별 평균 월급:"));
        searchPanel.add(groupByBox);
        searchPanel.add(new JLabel("검색 조건:"));
        searchPanel.add(departmentComboBox);
        searchPanel.add(genderComboBox);
        searchPanel.add(salaryField);
//        searchPanel.add(new JLabel("정렬 기준:"));
//        searchPanel.add(sortByBox);

        departmentComboBox.setEnabled(false); // 기본적으로 비활성화
        genderComboBox.setEnabled(false); // 기본적으로 비활성화
        groupByBox.setEnabled(false); // 기본적으로 비활성화

        searchPanel.add(searchButton);
        searchPanel.add(deleteButton);
        searchPanel.add(addButton);
        searchPanel.add(updateButton);

        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchCategoryBox.addActionListener(e -> updateSearchOptions());
        groupByBox.addActionListener(e -> updateSearchOptions());


        searchButton.addActionListener(e -> searchEmployees());
        deleteButton.addActionListener(e -> deleteEmployees());
        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());

        // 프로젝트 테이블 설정
        projectModel = new DefaultTableModel(new Object[]{"프로젝트 이름", "프로젝트 번호", "프로젝트 위치", "부서 번호"}, 0);
        projectTable = new JTable(projectModel);

        // 프로젝트 관련 버튼
        // 프로젝트 검색 패널 설정
        JPanel projectSearchPanel = new JPanel(new FlowLayout());
        projectSearchCategoryBox = new JComboBox<>(new String[]{"전체","프로젝트 이름", "프로젝트 번호", "프로젝트 위치", "부서 번호"});

        projectNameField = new JTextField(10);

        projectSearchButton = new JButton("검색");
        projectAddButton = new JButton("프로젝트 추가");
        projectUpdateButton = new JButton("프로젝트 수정");
        projectDeleteButton = new JButton("프로젝트 삭제");

        projectSearchPanel.add(new JLabel("검색 기준:"));
        projectSearchPanel.add(projectSearchCategoryBox);
        projectSearchPanel.add(projectNameField);
        projectSearchPanel.add(projectSearchButton);
        projectSearchPanel.add(projectAddButton);
        projectSearchPanel.add(projectUpdateButton);
        projectSearchPanel.add(projectDeleteButton);

        // 프로젝트 패널 설정
        JPanel projectPanel = new JPanel(new BorderLayout());
        projectPanel.add(projectSearchPanel, BorderLayout.NORTH);
        projectPanel.add(new JScrollPane(projectTable), BorderLayout.CENTER);

        add(projectPanel, BorderLayout.SOUTH);

        projectSearchButton.addActionListener(e -> searchProjects());
        projectAddButton.addActionListener(e -> addProject());
        projectUpdateButton.addActionListener(e -> updateProject());
        projectDeleteButton.addActionListener(e -> deleteProject());

        loadEmployeeData();
        loadProjectData();
    }

    /**
     * 부서 목록을 데이터베이스에서 가져와 콤보박스에 설정하는 메서드
     */
    private String[] getDepartments() {
        List<String> departments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Dname FROM DEPARTMENT")) {
            while (rs.next()) {
                departments.add(rs.getString("Dname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments.toArray(new String[0]);
    }

    /**
     * 검색 옵션 업데이트
     * 선택된 검색 범위에 따라 적절한 검색 조건 필드를 활성화/비활성화
     */
    private void updateSearchOptions() {
        String selectedCategory = (String) searchCategoryBox.getSelectedItem();
        String selectedGroupBy = (String) groupByBox.getSelectedItem();
        departmentComboBox.setEnabled(false);
        genderComboBox.setEnabled(false);
        salaryField.setEnabled(false);
        groupByBox.setEnabled(false);

        switch (selectedCategory) {
            case "부서" -> departmentComboBox.setEnabled(true);
            case "성별" -> genderComboBox.setEnabled(true);
            case "급여", "주소", "상사 주민번호", "생년월일", "이름", "주민번호" -> salaryField.setEnabled(true);
            case "평균 월급" -> {
                groupByBox.setEnabled(true);
                if ("상급자".equals(selectedGroupBy)) {
                    salaryField.setEnabled(true);
                }
            }
        }
    }


    /**
     * 테이블의 열을 설정하는 메서드
     */
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

    /**
     * 직원 데이터를 불러와 테이블에 표시하는 메서드
     */
    private void loadEmployeeData() {
        String query = """
            SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department, COALESCE(SUM(w.Hours), 0) AS TotalHours
            FROM EMPLOYEE e
            JOIN DEPARTMENT d ON e.Dno = d.Dnumber
            LEFT JOIN Works_on w ON e.Ssn = w.Essn
            GROUP BY e.Ssn, e.Fname, e.Minit, e.Lname, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname;
            """;

        model.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        false, rs.getString("Name"), rs.getString("Ssn"), rs.getDate("Bdate"),
                        rs.getString("Address"), rs.getString("Sex"), rs.getDouble("Salary"),
                        rs.getString("Super_ssn"), rs.getString("Department"),
                        rs.getDouble("TotalHours") // 총 작업 시간을 포함
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 조건에 따라 직원 데이터를 검색하고 테이블에 표시하는 메서드
     */
    private void searchEmployees() {
        String category = searchCategoryBox.getSelectedItem().toString();
        String groupBy = groupByBox.getSelectedItem().toString();
        String query = null;

        switch (category) {
            case "부서" -> query = """
            SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department, COALESCE(SUM(w.Hours), 0) AS TotalHours
            FROM EMPLOYEE e JOIN DEPARTMENT d ON e.Dno = d.Dnumber
            LEFT JOIN Works_on w ON e.Ssn = w.Essn
            WHERE d.Dname = ?
            GROUP BY e.Ssn, e.Fname, e.Minit, e.Lname, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname;
            """;
            case "성별" -> query = """
            SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department, COALESCE(SUM(w.Hours), 0) AS TotalHours
            FROM EMPLOYEE e JOIN DEPARTMENT d ON e.Dno = d.Dnumber
            LEFT JOIN Works_on w ON e.Ssn = w.Essn
            WHERE e.Sex = ?
            GROUP BY e.Ssn, e.Fname, e.Minit, e.Lname, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname;
            """;
            case "급여" -> query = """
            SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department, COALESCE(SUM(w.Hours), 0) AS TotalHours
            FROM EMPLOYEE e JOIN DEPARTMENT d ON e.Dno = d.Dnumber
            LEFT JOIN Works_on w ON e.Ssn = w.Essn
            WHERE e.Salary >= ?
            GROUP BY e.Ssn, e.Fname, e.Minit, e.Lname, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname;
            """;
            case "주소" -> query = """
            SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department, COALESCE(SUM(w.Hours), 0) AS TotalHours
            FROM EMPLOYEE e JOIN DEPARTMENT d ON e.Dno = d.Dnumber
            LEFT JOIN Works_on w ON e.Ssn = w.Essn
            WHERE e.Address LIKE ?
            GROUP BY e.Ssn, e.Fname, e.Minit, e.Lname, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname;
            """;
            case "상사 주민번호" -> query = """
            SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department, COALESCE(SUM(w.Hours), 0) AS TotalHours
            FROM EMPLOYEE e JOIN DEPARTMENT d ON e.Dno = d.Dnumber
            LEFT JOIN Works_on w ON e.Ssn = w.Essn
            WHERE e.Super_ssn = ?
            GROUP BY e.Ssn, e.Fname, e.Minit, e.Lname, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname;
            """;
            case "생년월일" -> query = """
            SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department, COALESCE(SUM(w.Hours), 0) AS TotalHours
            FROM EMPLOYEE e JOIN DEPARTMENT d ON e.Dno = d.Dnumber
            LEFT JOIN Works_on w ON e.Ssn = w.Essn
            WHERE e.Bdate = ?
            GROUP BY e.Ssn, e.Fname, e.Minit, e.Lname, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname;
            """;
            case "이름" -> query = """
            SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department, COALESCE(SUM(w.Hours), 0) AS TotalHours
            FROM EMPLOYEE e JOIN DEPARTMENT d ON e.Dno = d.Dnumber
            LEFT JOIN Works_on w ON e.Ssn = w.Essn
            WHERE CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) LIKE ?
            GROUP BY e.Ssn, e.Fname, e.Minit, e.Lname, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname;
            """;
            case "주민번호" -> query = """
            SELECT CONCAT(e.Fname, ' ', e.Minit, ' ', e.Lname) AS Name, e.Ssn, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname AS Department, COALESCE(SUM(w.Hours), 0) AS TotalHours
            FROM EMPLOYEE e JOIN DEPARTMENT d ON e.Dno = d.Dnumber
            LEFT JOIN Works_on w ON e.Ssn = w.Essn
            WHERE e.Ssn = ?
            GROUP BY e.Ssn, e.Fname, e.Minit, e.Lname, e.Bdate, e.Address, e.Sex, e.Salary, e.Super_ssn, d.Dname;
            """;
            case "평균 월급" -> {
                if ("상급자".equals(groupBy)) {
                    query = """
                SELECT CONCAT(s.Fname, ' ', s.Minit, ' ', s.Lname) AS Supervisor, AVG(e.Salary) AS AvgSalary
                FROM EMPLOYEE e
                JOIN EMPLOYEE s ON e.Super_ssn = s.Ssn
                WHERE e.Super_ssn = ?
                GROUP BY e.Super_ssn;
                """;
                } else if ("성별".equals(groupBy)) {
                    query = """
                SELECT e.Sex AS GroupCategory, AVG(e.Salary) AS AvgSalary
                FROM EMPLOYEE e
                GROUP BY e.Sex;
                """;
                } else if ("부서".equals(groupBy)) {
                    query = """
                SELECT d.Dname AS GroupCategory, AVG(e.Salary) AS AvgSalary
                FROM EMPLOYEE e
                JOIN DEPARTMENT d ON e.Dno = d.Dnumber
                GROUP BY d.Dname;
                """;
                }
            }
            default -> {
                loadEmployeeData();
                return;
            }
        }

        if (query != null) {
            model.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

            	if ("평균 월급".equals(category) && !"그룹 없음".equals(groupBy)) {
                    if ("상급자".equals(groupBy)) {
                        model.setColumnIdentifiers(new Object[]{"선택", "상급자", "평균 급여"});
                    } else if ("성별".equals(groupBy)) {
                        model.setColumnIdentifiers(new Object[]{"선택", "성별", "평균 급여"});
                    } else if ("부서".equals(groupBy)) {
                        model.setColumnIdentifiers(new Object[]{"선택", "부서", "평균 급여"});
                    }
                } else {
                    // 기본 컬럼으로 재설정
                    model.setColumnIdentifiers(new Object[]{"선택", "이름", "주민번호", "생년월일", "주소", "성별", "급여", "상사", "부서", "총 작업시간"});
                }

            

                       ResultSet rs = pstmt.executeQuery();
                       while (rs.next()) {
                           if ("평균 월급".equals(category) && !"그룹 없음".equals(groupBy)) {
                               if ("상급자".equals(groupBy)) {
                                   model.addRow(new Object[]{false, rs.getString("Supervisor"), rs.getDouble("AvgSalary")});
                               } else {
                                   model.addRow(new Object[]{false, rs.getString("GroupCategory"), rs.getDouble("AvgSalary")});
                               }
                           } else {
                               // 기존 데이터 로드 부분 (생략)
                           }
                       }
                   } catch (SQLException | NumberFormatException e) {
                       JOptionPane.showMessageDialog(this, "올바른 입력을 확인하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                   }
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
                String ssn = model.getValueAt(i, 2).toString();
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
        JTextField bDateField = new JTextField(model.getValueAt(selectedRow, 3).toString());
        JTextField addressField = new JTextField(model.getValueAt(selectedRow, 4).toString());
        JTextField sexField = new JTextField(model.getValueAt(selectedRow, 5).toString());
        JTextField salaryField = new JTextField(model.getValueAt(selectedRow, 6).toString());
        JTextField superSsnField = new JTextField(model.getValueAt(selectedRow, 7).toString());

        JComboBox<String> dnoComboBox = new JComboBox<>(getDepartments());
        dnoComboBox.setSelectedItem(model.getValueAt(selectedRow, 8).toString());

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("이름:"));
        panel.add(fNameField);
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
        panel.add(new JLabel("부서 이름:"));
        panel.add(dnoComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "직원 수정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String birthDateText = bDateField.getText().trim();
                Double salary;
                try {
                    salary = Double.parseDouble(salaryField.getText().trim());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "올바른 숫자 형식의 급여를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Date birthDate;
                try {
                    birthDate = Date.valueOf(birthDateText);
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, "올바른 날짜 형식 (YYYY-MM-DD)으로 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectedDepartment = (String) dnoComboBox.getSelectedItem();
                int departmentNumber = getDepartmentNumber(selectedDepartment);

                if (departmentNumber == -1) {
                    JOptionPane.showMessageDialog(this, "부서 번호를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String query = """
                UPDATE EMPLOYEE SET Fname = ?, Bdate = ?, Address = ?, Sex = ?, Salary = ?, Super_ssn = ?, Dno = ?, modified = CURRENT_TIMESTAMP
                WHERE Ssn = ?;
                """;

                try (Connection conn = DatabaseConnection.getConnection()) {
                    conn.setAutoCommit(false);

                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setString(1, fNameField.getText().trim());
                        pstmt.setDate(2, birthDate);
                        pstmt.setString(3, addressField.getText().trim());
                        pstmt.setString(4, sexField.getText().trim());
                        pstmt.setDouble(5, salary);
                        pstmt.setString(6, superSsnField.getText().trim());
                        pstmt.setInt(7, departmentNumber);
                        pstmt.setString(8, model.getValueAt(selectedRow, 2).toString().trim());

                        int rows = pstmt.executeUpdate();
                        if (rows > 0) {
                            conn.commit();
                            JOptionPane.showMessageDialog(this, "직원 정보가 수정되었습니다.");
                            loadEmployeeData();
                        } else {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "수정할 직원 정보를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "데이터베이스 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "예상치 못한 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 부서 이름에 맞는 부서 번호를 반환하는 메서드
     */
    private int getDepartmentNumber(String departmentName) {
        String query = "SELECT Dnumber FROM DEPARTMENT WHERE Dname = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, departmentName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Dnumber");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void loadProjectData() {
        String query = "SELECT Pname, Pnumber, Plocation, Dnum FROM PROJECT"; // 프로젝트 데이터 쿼리

        projectModel.setRowCount(0); // 기존 데이터 초기화

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
//                        false,
                        rs.getString("Pname"),
                        rs.getInt("Pnumber"),
                        rs.getString("Plocation"),
                        rs.getInt("Dnum")
                };
                projectModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchProjects() {
        String category = projectSearchCategoryBox.getSelectedItem().toString(); // 선택된 검색 기준
        String query = null;

        switch (category) {
            case "프로젝트 이름" -> query = """
            SELECT Pname AS ProjectName,
                   Pnumber AS ProjectNumber,
                   Plocation AS ProjectLocation,
                   Dnum AS DepartmentNumber
            FROM PROJECT p
            WHERE Pname LIKE ?;
        """;
            case "프로젝트 번호" -> query = """
            SELECT Pname AS ProjectName,
                   Pnumber AS ProjectNumber,
                   Plocation AS ProjectLocation,
                   Dnum AS DepartmentNumber
            FROM PROJECT p
            WHERE Pnumber = ?;
        """;
            case "프로젝트 위치" -> query = """
            SELECT Pname AS ProjectName,
                   Pnumber AS ProjectNumber,
                   Plocation AS ProjectLocation,
                   Dnum AS DepartmentNumber
            FROM PROJECT p
            WHERE Plocation LIKE ?;
        """;
            case "부서 번호" -> query = """
            SELECT Pname AS ProjectName,
                   Pnumber AS ProjectNumber,
                   Plocation AS ProjectLocation,
                   Dnum AS DepartmentNumber
            FROM PROJECT p
            WHERE Dnum = ?;
        """;
            default -> {
                loadProjectData(); // 기본 데이터 로드 메서드
                return;
            }
        }

        if (query != null) {
            projectModel.setRowCount(0); // 기존 데이터 초기화
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                // 사용자 입력에 따라 쿼리 파라미터 설정
                switch (category) {
                    case "프로젝트 이름":
                        pstmt.setString(1, "%" + projectNameField.getText() + "%");
                        break;
                    case "프로젝트 번호":
                        pstmt.setInt(1, Integer.parseInt(projectNameField.getText())); // 정수형으로 변환
                        break;
                    case "프로젝트 위치":
                        pstmt.setString(1, "%" + projectNameField.getText() + "%");
                        break;
                    case "부서 번호":
                        pstmt.setInt(1, Integer.parseInt(projectNameField.getText())); // 정수형으로 변환
                        break;
                }

                // 쿼리 실행 및 결과 처리
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    projectModel.addRow(new Object[]{
                            rs.getString("ProjectName"),
                            rs.getInt("ProjectNumber"),
                            rs.getString("ProjectLocation"),
                            rs.getInt("DepartmentNumber")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "검색 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "올바른 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void addProject() {
        JTextField projectNameField = new JTextField();
        JTextField projectNumberField = new JTextField();
        JTextField projectLocationField = new JTextField();
        JTextField departmentNumberField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("프로젝트 이름:"));
        panel.add(projectNameField);
        panel.add(new JLabel("프로젝트 번호:"));
        panel.add(projectNumberField);
        panel.add(new JLabel("프로젝트 위치:"));
        panel.add(projectLocationField);
        panel.add(new JLabel("부서 번호:"));
        panel.add(departmentNumberField);

        int result = JOptionPane.showConfirmDialog(this, panel, "새 프로젝트 추가", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // 입력값 검증
            String projectName = projectNameField.getText().trim();
            String projectNumberText = projectNumberField.getText().trim();
            String projectLocation = projectLocationField.getText().trim();
            String departmentNumberText = departmentNumberField.getText().trim();

            if (projectName.isEmpty() || projectNumberText.isEmpty() || projectLocation.isEmpty() || departmentNumberText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int projectNumber;
            int departmentNumber;

            try {
                projectNumber = Integer.parseInt(projectNumberText);
                departmentNumber = Integer.parseInt(departmentNumberText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "프로젝트 번호와 부서 번호는 정수여야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = """
            INSERT INTO PROJECT (Pname, Pnumber, Plocation, Dnum)
            VALUES (?, ?, ?, ?);
        """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, projectName);
                pstmt.setInt(2, projectNumber);
                pstmt.setString(3, projectLocation);
                pstmt.setInt(4, departmentNumber);

                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "새 프로젝트가 추가되었습니다.");
                    loadProjectData(); // 프로젝트 데이터를 새로 고침
                } else {
                    JOptionPane.showMessageDialog(this, "프로젝트 추가에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void updateProject() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "수정할 프로젝트를 선택하세요.");
            return;
        }

        // 선택된 행의 현재 값을 가져와서 텍스트 필드에 초기화
        JTextField projectNameField = new JTextField(projectModel.getValueAt(selectedRow, 0).toString());
        JTextField projectNumberField = new JTextField(projectModel.getValueAt(selectedRow, 1).toString());
        JTextField projectLocationField = new JTextField(projectModel.getValueAt(selectedRow, 2).toString());
        JTextField departmentNumberField = new JTextField(projectModel.getValueAt(selectedRow, 3).toString());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("프로젝트 이름:"));
        panel.add(projectNameField);
        panel.add(new JLabel("프로젝트 번호:"));
        panel.add(projectNumberField);
        panel.add(new JLabel("프로젝트 위치:"));
        panel.add(projectLocationField);
        panel.add(new JLabel("부서 번호:"));
        panel.add(departmentNumberField);

        int result = JOptionPane.showConfirmDialog(this, panel, "프로젝트 수정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String query = """
            UPDATE PROJECT SET Pname = ?, Plocation = ?, Dnum = ?
            WHERE Pnumber = ?;
        """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, projectNameField.getText().trim());
                pstmt.setString(2, projectLocationField.getText().trim());
                pstmt.setInt(3, Integer.parseInt(departmentNumberField.getText().trim()));
                pstmt.setInt(4, Integer.parseInt(projectNumberField.getText().trim()));

                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "프로젝트 정보가 수정되었습니다.");
                    loadProjectData(); // 프로젝트 데이터를 새로 고침
                } else {
                    JOptionPane.showMessageDialog(this, "수정할 프로젝트 정보를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "부서 번호와 프로젝트 번호는 정수여야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void deleteProject() {
        List<String> projectNumbersToDelete = new ArrayList<>();

        // 선택된 프로젝트의 번호를 수집
        for (int i = 0; i < projectTable.getRowCount(); i++) {
            if (projectTable.isRowSelected(i)) { // 해당 행이 선택되었는지 확인
                String projectNumber = projectModel.getValueAt(i, 1).toString(); // 프로젝트 번호는 1번 인덱스에 위치
                projectNumbersToDelete.add(projectNumber);
            }
        }

        if (projectNumbersToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "삭제할 프로젝트를 선택하세요.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "선택한 프로젝트를 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            String query = "DELETE FROM PROJECT WHERE Pnumber = ?"; // 프로젝트 번호로 삭제
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                for (String projectNumber : projectNumbersToDelete) {
                    pstmt.setString(1, projectNumber);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            conn.commit();
            loadProjectData(); // 프로젝트 데이터 새로 고침
            JOptionPane.showMessageDialog(this, "선택된 프로젝트가 삭제되었습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "삭제 중 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main management = new Main();
            management.setVisible(true);
        });
    }
}