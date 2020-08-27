package javasample090;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SampleClass090
 */
@WebServlet("/SampleClass090")
public class SampleClass090 extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SampleClass090() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// データベースに接続し、テーブルから値を取得する
		Connection conn = null;

		// 日記リストの定義
		List<List<String>> diaryList = new ArrayList<>();

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			// jdbcを読み込んで使える状態にする
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/javasample", "root", "root");

			// まずSQL自体をString型で定義
			String sql = "SELECT *, DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s') AS post_date FROM diary " + "WHERE delete_flg != 1 ORDER BY created_at DESC";

			// SQLで実行するため準備
			stmt = conn.prepareStatement(sql);
			// SQLの実行
			rs = stmt.executeQuery();

			// SQLを実行した結果として得られた各レコードから１つずつレコードを取得
			while(rs.next()) {
				String postDate = rs.getString("post_date");
				String content = rs.getString("content");

				// List型のdiaryに追加していく
				List<String> diary = new ArrayList<>();
				diary.add(postDate);
				diary.add(content);

				// 各レコードの情報がセットされているdiaryという変数をdiaryListに詰める
				diaryList.add(diary);
			}

		} catch (SQLException e ) {
			// DBへの接続や。SQL処理で失敗が起きた時
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// JDBCドライバを見つけられない時
			e.printStackTrace();
		} finally {
			// 最後に必ず実行する
			try {
				if(stmt != null) {
					stmt.close();
				}
				if(rs != null) {
					rs.close();
				}
				// DBとの接続を切断する
				if(conn != null) {
					conn.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		// htmlの内容を１行１行出力していく
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Diary</title>");
		out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" />");
		// WebContentフォルダの下においたファイルは、
		// http://<サーバ名>/<プロジェクト名>/ の後ろにそのファイル名をつけるだけでアクセスできるようになる。
		// 実際の現場ではWebContentフォルダ下にcssフォルダやjsフォルダなどを作ってそこに配置していく場合が多い。
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/sample09.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<header>");
		out.println("<div class=\"logo\">Diary</div>");
		out.println("</header>");
		out.println("<div class=\"main\">");
		out.println("<div class=\"index_type_virtualcard_circle\">");
		out.println("</div>");
		// 日記投稿フォーム
		out.println("<form method=\"post\">");
		out.println("<div class=\"mt30\">");
		out.println("<label for=\"content\">投稿内容：</label>");
		out.println("<textarea id=\"content\" class=\"content\" name=\"content\"></textarea>");
		out.println("</div>");
		out.println("<div class=\"button-area\">");
		out.println("<button type=\"submit\" class=\"btn btn-success mt30 relative float-right\">日記を投稿</button>");
		out.println("</div>");
		out.println("</form>");
		out.println("<h1 class=\"diary-list-title\">日記一覧</h1>");
		// 日記一覧表示
		out.println("<div>");
		for(List<String> diary : diaryList) {
			out.println("<div class=\"diary-content\">");
			out.println("<div class=\"post-date\">");
			// ここで、投稿日の情報を取り出す
			out.println(diary.get(0));
			out.println("</div>");
			out.print("<p>");
			// ここで、日記の内容（content）の情報を取り出す。
			out.println(diary.get(1));
			out.print("</p>");
			out.println("</div>");
		}
		out.println("</div>");
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");

	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// POSTされてきたフォームデータを読み込む際に文字化けしないように文字コードをセット
		request.setCharacterEncoding("UTF-8");

		// DBに接続し、ブラウザからのPOSTリクエストで送られてきた日記の内容をテーブルに保存
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			//　現在時刻
			Timestamp now = new Timestamp(System.currentTimeMillis());
			// JDBCを読み込んで使える状態にする
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/javasample", "root", "root");

			//SQL文を準備
			String sql = "INSERT INTO diary (content, delete_flg, created_at, updated_at) VALUES (?, 0, ?, ?)";
			stmt = conn.prepareStatement(sql);

			// ?を左から順にセットしていく
			stmt.setString(1, request.getParameter("content"));
			stmt.setTimestamp(2, now);
			stmt.setTimestamp(3, now);
			stmt.executeUpdate();  // select文以外のSQLはexcuteUpdate()を使用する

		} catch (SQLException e) {
			// DBへの接続やSQL接続で失敗したとき
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// JDNCドライバが見つけられない時
			e.printStackTrace();
		} finally {
			try {
				if(stmt != null) {
					stmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// 日記の内容を投稿した後は、また日記の一覧が出て欲しいので、
		// 日記の表示画面にリダイレクトする
		String url = "SampleClass090";
		response.sendRedirect(url);
	}

}
