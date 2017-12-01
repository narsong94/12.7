package com.prj.web.dao.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.prj.web.dao.VotingDao;
import com.prj.web.dao.VotingLikeDao;
import com.prj.web.entity.Voting;
import com.prj.web.entity.VotingLike;

public class SpringVotingDao implements VotingDao, VotingLikeDao {

	@Autowired
	private JdbcTemplate template;

	@Override
	public List<Voting> getList(int page, String query) {
		String sql = "select * from Voting where title like ? order by date DESC limit ?,10";

		List<Voting> list = template.query(sql, new Object[] { "%" + query + "%", (page - 1) * 10 },
				BeanPropertyRowMapper.newInstance(Voting.class));
		return list;
	}

	@Override
	public Voting getVoting(String id) {
		String sql = "select * from Voting where id = ?";

		Voting voting = template.queryForObject(sql, new Object[] { id },
				BeanPropertyRowMapper.newInstance(Voting.class));
		return voting;
	}

	public List<String> getImgs(String id) {

		List<String> list = new ArrayList<String>();

		// �׷� ���⼭ �� id�� content�� ���� list�� ����.
		String sql = "select content from Voting where id = ?";

		String next = template.queryForObject(sql, new Object[] { id }, String.class);
		// content�� �����ͼ�
		Pattern nonValidPattern = Pattern.compile("<img[^>]*src=[\\\"']?([^>\\\"']+)[\\\"']?[^>]*>");
		int imgCnt = 0;
		String content = "";
		Matcher matcher = nonValidPattern.matcher(next);
		while (matcher.find()) {
			content = matcher.group(1);
			imgCnt++;
			list.add(content);
			if (imgCnt == 3) {
				break;
			}
		}
		String noImg = "data:image/gif;base64,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";
		if (imgCnt == 1) {
			list.add(noImg);
			list.add(noImg);
		} else {
			list.add(noImg);
		}
		return list;
	}

	@Override
	public Voting getVotingPrev(String id) {
		String sql = "select * from Voting where id < CAST(? as UNSIGNED) order by date DESC limit 1";

		try {
			return template.queryForObject(sql, new Object[] { id }, BeanPropertyRowMapper.newInstance(Voting.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Voting getVotingNext(String id) {
		String sql = "select * from Voting where id > CAST(? as UNSIGNED) order by date ASC limit 1";

		try {
			return template.queryForObject(sql, new Object[] { id }, BeanPropertyRowMapper.newInstance(Voting.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public int getVotingCount() {
		String sql = "SELECT COUNT(id) as count FROM Voting";

		int count = template.queryForObject(sql, Integer.class);
		return count;
	}

	@Override
	public int update(String id, Voting voting) {
		String sql = "update Voting set title = ?, content = ? where id = ?;";

		int result = template.update(sql, voting.getTitle(), voting.getContent(), id);

		return result;
	}

	@Override
	public int updateHit(String id) {
		String sql = "update Voting set hit = ifnull(hit,0)+1 where id = ?;";

		return template.update(sql, id);
	}

	@Override
	public int insert(String title, String content, String writerId) {
		return insert(new Voting(title, content, writerId));
	}

	@Override
	public int insert(Voting voting) {

		String sql = "insert into Voting(id, title, content, writer_id, hit) values(?, ?, ?, ?, 0);";

		int insert = template.update(sql, getNextId(), voting.getTitle(), voting.getContent(), voting.getWriterId());

		return insert;
	}

	@Override
	public int getNextId() {
		String sql = "select ifnull(MAX(CAST(id as unsigned)),0)+1 from Voting";

		int nextId = template.queryForObject(sql, Integer.class);

		return nextId;
	}

	@Override
	public int delete(String id) {
		String sql = "delete from Voting where id = ?";

		int del = template.update(sql, id);

		return del;
	}

/*	@Override
	public VotingLike updateLike(String id, String num) {
		String sql = "update VotingLike set like" + num + "= ifnull(like" + num + ",0)+1 where votingId = ?;";

		template.update(sql, id);
		try {
			return getVotingLike(id, num);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}*/

	@Override
	public int getVotingLike(String id, String num) {
		String sql = "select count(like"+num+") from VotingLike where votingId = ? and like"+num+"=1;";
		try {
			int like = template.queryForObject(sql, new Object[] { id }, Integer.class);
			return like;
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

/*	@Override
	public int setVotingLike(int id) {
		String sql = "insert into VotingLike(votingId, like1, like2, like3) values(?, 0, 0, 0);";

		int insert = template.update(sql, id);

		return insert;
	}*/

	@Override
	public int getVoteUser(String userId) {
		String sql = "select * from VotingLike where userId = ?;";
		VotingLike like = null;
		
		try {
			like = template.queryForObject(sql, new Object[] { userId },
					BeanPropertyRowMapper.newInstance(VotingLike.class));
			if (like != null) 
				return 1;
			else 
				return 0;
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	@Override
	public int setVotingUserLike(String id, String userId, String num) {
		String sql = "";
		if(num.equals("1")) {
			sql = "insert into VotingLike(votingId, like1, like2, like3, userId) values(?, 1, 0, 0, ?);";
		}else if(num.equals("2")) {
			sql = "insert into VotingLike(votingId, like1, like2, like3, userId) values(?, 0, 1, 0, ?);";
		}else {
			sql = "insert into VotingLike(votingId, like1, like2, like3, userId) values(?, 0, 0, 1, ?);";
		}
		
		template.update(sql, id, userId);
		
		try {
			return getVotingLike(id, num);
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	@Override
	public VotingLike getVotingLike(String id) {
		String sql = "select count(case when like1='1' then 1 end) as like1, count(case when like2='1' then 1 end) as like2, count(case when like3='1' then 1 end) as like3 from VotingLike where votingId=?;";
		try {
			VotingLike like = template.queryForObject(sql, new Object[] { id },
					BeanPropertyRowMapper.newInstance(VotingLike.class));
			return like;
		}catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
