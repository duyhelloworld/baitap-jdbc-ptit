import java.sql.Connection;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class App extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JTextField txtMaSV, txtHoTen, txtLop, txtGPA;
    private JButton btnHienThi, btnThem, btnCapNhat, btnXoa, btnReset;
    private JTable table;
    private DefaultTableModel model;
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public App() {
        super("Quản lý sinh viên");
        JPanel pnNorth = new JPanel();
        pnNorth.add(txtMaSV = new JTextField(20));
        pnNorth.add(txtHoTen = new JTextField(20));
        pnNorth.add(txtLop = new JTextField(20));
        pnNorth.add(txtGPA = new JTextField(20));
        JPanel pnSouth = new JPanel();
        pnSouth.add(btnHienThi = new JButton("Hiển thị"));
        pnSouth.add(btnThem = new JButton("Thêm"));
        pnSouth.add(btnCapNhat = new JButton("Cập nhật"));
        pnSouth.add(btnXoa = new JButton("Xóa"));
        pnSouth.add(btnReset = new JButton("Reset"));
        JPanel pnCenter = new JPanel();
        pnCenter.setLayout(new BorderLayout());
        pnCenter.add(new JScrollPane(table = new JTable(model = new DefaultTableModel())), BorderLayout.CENTER);
        model.addColumn("Mã SV");
        model.addColumn("Họ tên");
        model.addColumn("Lớp");
        model.addColumn("GPA");
        add(pnNorth, BorderLayout.NORTH);
        add(pnSouth, BorderLayout.SOUTH);
        add(pnCenter, BorderLayout.CENTER);
        btnHienThi.addActionListener(this);
        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnXoa.addActionListener(this);
        btnReset.addActionListener(this);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        conn = AppConnection.getConn();
    }

    public static void main(String[] args) {
        new App();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnHienThi) {
            try {
                ps = conn.prepareStatement("SELECT * FROM SinhVien");
                rs = ps.executeQuery();
                model.setRowCount(0);
                while (rs.next()) {
                    Vector<String> vec = new Vector<String>();
                    vec.add(rs.getString("masv"));
                    vec.add(rs.getString("hoten"));
                    vec.add(rs.getString("lop"));
                    vec.add(rs.getFloat("gpa") + "");
                    model.addRow(vec);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == btnThem) {
            String maSV = JOptionPane.showInputDialog(this, "Nhập mã sinh viên:");
            String hoTen = JOptionPane.showInputDialog(this, "Nhập họ tên:");
            String lop = JOptionPane.showInputDialog(this, "Nhập lớp:");
            String gpa = JOptionPane.showInputDialog(this, "Nhập GPA:");
            if (maSV == null || hoTen == null || lop == null || gpa == null) {
                return;
            }
            maSV = maSV.trim();
            hoTen = hoTen.trim();
            lop = lop.trim();
            gpa = gpa.trim();
            if (maSV.equals("") || hoTen.equals("") || lop.equals("") || gpa.equals("")) {
                return;
            }
            try {
                ps = conn.prepareStatement("SELECT * FROM SinhVien WHERE masv = ?");
                ps.setString(1, maSV);
                rs = ps.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Mã sinh viên đã tồn tại!");
                    return;
                }
                ps = conn.prepareStatement("INSERT INTO SinhVien VALUES (?, ?, ?, ?)");
                ps.setString(1, maSV);
                ps.setString(2, hoTen);
                ps.setString(3, lop);
                ps.setFloat(4, Float.parseFloat(gpa));
                ps.executeUpdate();
                btnHienThi.doClick();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == btnCapNhat) {
            int row = table.getSelectedRow();
            if (row == -1) {
                return;
            }
            String maSV = (String) model.getValueAt(row, 0);
            String hoTen = JOptionPane.showInputDialog(this, "Nhập họ tên:", model.getValueAt(row, 1));
            String lop = JOptionPane.showInputDialog(this, "Nhập lớp:", model.getValueAt(row, 2));
            String gpa = JOptionPane.showInputDialog(this, "Nhập GPA:", model.getValueAt(row, 3));
            if (hoTen == null || lop == null || gpa == null) {
                return;
            }
            hoTen = hoTen.trim();
            lop = lop.trim();
            gpa = gpa.trim();
            if (hoTen.equals("") || lop.equals("") || gpa.equals("")) {
                return;
            }
            try {
                ps = conn.prepareStatement("UPDATE SinhVien SET hoten = ?, lop = ?, gpa = ? WHERE masv = ?");
                ps.setString(1, hoTen);
                ps.setString(2, lop);
                ps.setFloat(3, Float.parseFloat(gpa));
                ps.setString(4, maSV);
                ps.executeUpdate();
                btnHienThi.doClick();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == btnXoa) {
            int row = table.getSelectedRow();
            if (row == -1) {
                return;
            }
            String maSV = (String) model.getValueAt(row, 0);
            try {
                ps = conn.prepareStatement("DELETE FROM SinhVien WHERE masv = ?");
                ps.setString(1, maSV);
                ps.executeUpdate();
                btnHienThi.doClick();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == btnReset) {
            txtMaSV.setText("");
            txtHoTen.setText("");
            txtLop.setText("");
            txtGPA.setText("");
        }
    }
}
