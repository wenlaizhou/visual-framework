package cn.framework.db;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * project code
 * package cn.framework.db
 * create at 16-3-8 下午6:11
 *
 * @author wenlai
 */
public class DruidTest extends HttpServlet {


    @Autowired
    @Qualifier("someBeanId")
    DruidDataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            PreparedStatement statement = dataSource.getConnection().prepareStatement("select now();");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                resp.getWriter().append(rs.getObject(1).toString());
            }
        }
        catch (Exception x) {
            x.printStackTrace();
        }
    }
}
