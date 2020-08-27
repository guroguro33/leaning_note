package javasample12;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class JspSample
 */
@WebServlet("/JspSample")
public class JspSample extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public JspSample() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//	今回は　JSPクラスや
		//	DAOクラス(Data Access Object　Javaでのデータベースへの接続を記載しておくもの)
		//	DTOクラス(Data Transfer Object データなどの様々な値を転送するための格納庫のようなもの)
		//	を使用して、MVCモデルを使った実装方法を見ていく

		//	DAOをインスタンス化してこのクラスで使用できる状態にする
		DiaryDAO diaryDAO = new DiaryDAO();

		//	今回はDAOがModelに相当し、このサーブレットクラスがControllerに相当する。
		//	画面表示を行うJSPがViewに相当する。
		//	実際の現場ではDAOを介してDBに接続し、いろんな計算処理などを行うことが多いが、
		//	その場合はそれたの計算処理を行う部分もModelに当たる。

		//	DAOを使ってDBに接続し、その結果をDTOにセットして取得する。
		List<Diary> diaryList = diaryDAO.findAll();
		//	JSP側で、今回取得したデータを表示できるようにリクエストの内容に変数名と値をセットしておく。
		//	request.setAttributeというメソッドを使う
		request.setAttribute("diaryList", diaryList);

		//	他にもセッションに変数名や値をセットしたり、
		// セッションから値を取得する場合は下記のような書き方もできる。
		//	HttpSession session = request.getSession();
		//	session.setAttribute("セッションにセットする変数名", value);
		//	session.getAttribute("セッションから値を取得したい変数名");

		// JSPファイルに対してブラウザから直接アクセスさせたくない場合は、
		// ブラウザ等の外部から内部のファイルにアクセスできないWEB-INFというフォルダの下におくことが多い。
		// RequestDispatcherとは、ブラウザから受け取ったリクエストをサーバー上の他のファイルに渡す時に使う。
		RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/jspsample.jsp");
		//	RequestDispatcherで定義されたforwardという方法を使って、サーバー上の別のファイルに処理を受け渡すことができる。
		//	このサーブレットクラスでレスポンスを作るのではなく、処理を渡した先のファイルでレスポンスを作っていくことになる。
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		// DAOには、DiaryというDTOを使ってデータを受け渡しているので、
		// Diaryクラスから生成したインスタンスに値をセットしていく。

		Diary diary = new Diary();
		diary.setContent(request.getParameter("content"));
		diary.setDelete_flg(0);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		diary.setCreated_at(now);
		diary.setUpdated_at(now);

		//	DAOをインスタンス化して、このクラスで使えるようにする
		DiaryDAO diaryDAO = new DiaryDAO();
		// resultの内容によってDB登録が成功したかどうか判定、処理を分ける場合もあるが、今回はそのまま画面表示させる
		boolean result = diaryDAO.create(diary);

		// 日記表示画面にリダイレクトする
		 String url = "JspSample";
		 response.sendRedirect(url);

	}

}
