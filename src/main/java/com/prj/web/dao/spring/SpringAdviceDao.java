package com.prj.web.dao.spring;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.prj.web.dao.AdviceCommentDao;
import com.prj.web.dao.AdviceDao;
import com.prj.web.dao.AdviceLikeDao;
import com.prj.web.dao.VotingCommentDao;
import com.prj.web.entity.Advice;
import com.prj.web.entity.Comment;

public class SpringAdviceDao implements AdviceDao, AdviceCommentDao, AdviceLikeDao {

	@Autowired
	private JdbcTemplate template;

	@Override
	public List<Advice> getList(int page, String query) {
		String sql = "select * from Advice where title like ? order by date DESC limit ?,10";

		List<Advice> list = template.query(sql, new Object[] { "%"+query+"%", (page - 1) * 10 },
				BeanPropertyRowMapper.newInstance(Advice.class));

		return list;
	}

	@Override
	public Advice getAdvice(String id) {
		String sql = "select * from Advice where id = ?";

		Advice advice = template.queryForObject(sql, new Object[] { id }, BeanPropertyRowMapper.newInstance(Advice.class));
		return advice;
	}

	@Override
	public Advice getAdvicePrev(String id) {
		String sql = "select * from Advice where id < CAST(? as UNSIGNED) order by date DESC limit 1";

		try {
			return template.queryForObject(sql, new Object[] { id }, BeanPropertyRowMapper.newInstance(Advice.class));
		} catch(EmptyResultDataAccessException e) {			
			return null;
		}
	}

	@Override
	public Advice getAdviceNext(String id) {
		String sql = "select * from Advice where id > CAST(? as UNSIGNED) order by date ASC limit 1";

		try {
			return template.queryForObject(sql, new Object[] { id }, BeanPropertyRowMapper.newInstance(Advice.class));
		} catch(EmptyResultDataAccessException e) {			
			return null;
		}
	}

	@Override
	public int getAdviceCount() {
		String sql = "SELECT COUNT(id) as count FROM Advice";
		
		int count = template.queryForObject(sql, Integer.class);
		return count;
	}

	@Override
	public int update(String id, Advice advice) {
		String sql = "update Advice set title = ?, content = ? where id = ?;";

		int result = template.update(sql, advice.getTitle(), advice.getContent(), id);
		
		return result;
	}

	@Override
	public int updateHit(String id) {
		String sql = "update Advice set hit = ifnull(hit,0)+1 where id = ?;";
		
		return template.update(sql, id);
	}

	@Override
	public int insert(String title, String content, String writerId) {
		return insert(new Advice(title, content, writerId));
	}

	@Override
	public int insert(Advice advice) {

		String sql = "insert into Advice(id, title, content, writer_id, hit) values(?, ?, ?, ?, 0);";
		
		int insert = template.update(sql, 
						getNextId(), 
						advice.getTitle(), 
						advice.getContent(), 
						advice.getWriterId());		
				
		return insert;
	}

	@Override
	public int getNextId() {
		String sql = "select ifnull(MAX(CAST(id as unsigned)),0)+1 from Advice";
		
		int nextId = template.queryForObject(sql, Integer.class);
		
		return nextId;
	}

	@Override
	public int delete(String id) {
		String sql = "delete from Advice where id = ?";
		
		int del = template.update(sql, id);
		
		return del;
	}

	@Override
	public List<Advice> getPrevAdviceList(String id, Date date) {
		String sql = "select * from Advice where writer_id = ? and date < ? order by date desc limit 3;";

		try {
			return template.query(sql, new Object[] { id, date },
				BeanPropertyRowMapper.newInstance(Advice.class));
		}catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	//-------------- Advice 댓글 start --------------//
	
	@Override
	public int commentInsert(String content, String adviceId, String writerId) {
		String sql = "insert into AdviceComment(content,adviceId,writerId) values(?,?,?)";
	      int result = template.update(sql,content,adviceId,writerId);
	      
	      return result;
	}

	@Override
	public List<Comment> getCommentList(String adviceId) {
		String sql = "select * from AdviceComment where adviceId = ? order by id asc";
	      List<Comment> list = template.query(sql, new Object[] {adviceId},
	            BeanPropertyRowMapper.newInstance(Comment.class));
	      return list;
	}

	@Override
	public List<Comment> getUpdateCommentList(String adviceId) {
		String sql = "select * from AdviceComment where adviceId = ? order by id asc";
	      List<Comment> list = template.query(sql, new Object[] {adviceId},
	            BeanPropertyRowMapper.newInstance(Comment.class));
	      return list;
	}

	@Override
	public int insert(String adviceId, String writerId) {
		System.out.println("insert - adviceId , writerId : "+adviceId+","+writerId);
		String sql = "insert into AdviceLikeWriterId(adviceId,WriterId) values(?,?)";
		int result = template.update(sql, adviceId, writerId);
		return result;
	}

	@Override
	public int updateLikeCount(String adviceId) {
		String sql = "update Advice set likeNum=likeNum+1 where id =?";
		int result = template.update(sql, adviceId);
		return result;
	}

	@Override
	public int check(String adviceId, String writerId) {
		System.out.println("check - adviceId , writerId : "+adviceId+","+writerId);
		int id = 0;
		String sql = "select id from AdviceLikeWriterId where writerId = ? and adviceId = ?";
		try {
			id = template.queryForObject(sql, new Object[] {writerId, adviceId}, Integer.class);
			return id;
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}catch (Exception e) {
			return -10;
		}
	}

	@Override
	public int count(String adviceId, String writerId) {
		int count = 0;
		String sql = "select likeNum from Advice where id = ?";
		try {
			count = template.queryForObject(sql, new Object[] {adviceId}, Integer.class);
			return count;
		} catch (Exception e) {
			System.out.println("count조회에 실패하였습니다.");
			return 0;
		}
	}
}
