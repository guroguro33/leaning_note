package javasample12;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//	DBを操作する処理をDAOに全て書いておくことで、
//	サーブレットクラスに何度もDB操作の処理を書かなくて済む。
public class DiaryDAO {

	//	DB接続はいろんなメソッドで行うため、毎回書かなくていいように定数を定義
	private final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
	private final String JDBC_URL = "jdbc:mysql://localhost:8889/javasample";
	private final String DB_USER = "root";
	private final String DB_PW = "root";

//	DBに登録してある日記を全件取得
//	Diaryというクラスは、DBから取得してきた複数のレコードの中の、各レコードの内容をセットしておく。
//	呼び出し元には全レコードの情報を返したいので、
//	Diaryというクラスをリストにして戻せるようにList<diary>を戻り値の型として定義しておく
	public List<Diary> findAll() {
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			List<Diary> diaryList = new ArrayList<>();

			try {
				//	jdbcを読み込む
				Class.forName(DRIVER_NAME);

				//	DriverManagerクラスのgetConnectionメソッドを使ってDBに接続
				conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PW);

				//	まずSQL文をString型で定義
				//	全カラムを取得して、DTOであるDiaryクラスにあとでセットしていく
				String sql = "SELECT * FROM diary WHERE delete_flg != 1 ORDER BY created_at DESC";

				//	SQL実行の準備
				stmt = conn.prepareStatement(sql);
				// SQL実行
				rs = stmt.executeQuery();

				//	SQLを実行した結果として得られた各レコードから１つずつデータを取得
					while(rs.next()) {
						Diary diary = new Diary();

						diary.setId(rs.getInt("id"));
						diary.setContent(rs.getString("content"));
						diary.setDelete_flg(rs.getInt("delete_flg"));
						diary.setCreated_at(rs.getTimestamp("created_at"));
						diary.setUpdated_at(rs.getTimestamp("updated_at"));

						//	各レコードの情報がセットされているdiaryを、
						//	さらに、全レコードのリストであるdiaryListに詰め込んでいく
						diaryList.add(diary);
					}
			} catch (SQLException e) {
				//	DBへの接続や、SQL処理で失敗が起きた時
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				//	JDBCドライバが見つけられない時
				e.printStackTrace();
			} finally {
				try {
					if(stmt != null) {
						stmt.close();
					}
					if(rs != null) {
						rs.close();
					}
					//	DBとの接続を切断する
					if(conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return diaryList;
	}

	//	DBに日記の内容を登録する
	//	ちゃんと登録できたかどうかをtrue/falseで呼び出し元に返すため、boolean型を戻り値の型として定義しておく
	//	またデータの移動を行うDiaryクラス(DTO)を引数で受けて、その内容をDBへ登録していく
	public boolean create(Diary diary) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			//	jdbcを読み込んで使える状態にする
			Class.forName(DRIVER_NAME);
			//	DriverManagerクラスのgetConnectionメソッドを使って、DBへ接続
			conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PW);

			//	INSERT文
			String sql = "INSERT INTO diary (content, delete_flg, created_at, updated_at) VALUES (?, ?, ?, ?)";
			//	SQLを実行する準備
			stmt = conn.prepareStatement(sql);
			//	SQLで?を入れたところに値を左から順番にセットしていく
			stmt.setString(1, diary.getContent());
			stmt.setInt(2, diary.getDelete_flg());
			stmt.setTimestamp(3, diary.getCreated_at());
			stmt.setTimestamp(4, diary.getUpdated_at());

			//	executeUpdate()は、成功したら、変更した行の数を返してくる
			//	この場合、insertに成功すれば1、失敗すれば0が返ってくる。
			int result = stmt.executeUpdate();
			if(result != 1) {
				return false;
			}

		} catch (SQLException e) {
			//	DBへの接続やSQL処理で失敗が起きた時
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			//	JDBCドライバが見つけられない時
			e.printStackTrace();
			return false;
		} finally {
			try {
				if(stmt != null) {
					stmt.close();
				}
				//	DBとの接続を切断する
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
