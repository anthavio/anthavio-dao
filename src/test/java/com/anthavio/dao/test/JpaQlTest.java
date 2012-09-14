package com.anthavio.dao.test;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.testng.annotations.Test;

import com.anthavio.aspect.ApiPolicyOverride;
import com.anthavio.dao.search.JpaQlSearch;
import com.anthavio.dao.search.criteria.LikeCriteria;
import com.anthavio.dao.search.criteria.LikeSearchType;
import com.anthavio.dao.search.criteria.Operator;
import com.anthavio.dao.test.entity.Employee;

@ApiPolicyOverride
public class JpaQlTest extends BaseJpaTest {

	@Test
	public void testMulti() {
		JpaQlSearch search = new JpaQlSearch();
		search.from("Employee e");
		search.eq("e.lastName", null, "Himmler", "", null, "Gimli");
		System.out.println(search);
	}

	@Test
	public void testLike() {
		int cc = 0;
		int pc = 0;
		JpaQlSearch search = new JpaQlSearch();
		search.from("Employee e");

		LikeCriteria like = new LikeCriteria("imm", "mml"); //BOTH is default
		search.like("e.lastName", like);
		cc += 1;
		pc += 2;
		assertThat(search.getCriterias().size()).isEqualTo(cc);
		assertThat(search.getParameters().size()).isEqualTo(pc);

		List list = search.getResultList(em);
		assertThat(list.size()).isEqualTo(1);

		search.clearCriterias();
		LikeCriteria like2 = new LikeCriteria("", null, "", null);
		search.like("e.lastName", like2);

		assertThat(search.getCriterias().size()).isEqualTo(0);
		assertThat(search.getParameters().size()).isEqualTo(0);

		search.clearCriterias();
		LikeCriteria like3 = new LikeCriteria(LikeSearchType.BOTH, null, "", null);
		search.like("e.lastName", like3);
		assertThat(search.getCriterias().size()).isEqualTo(0);
		assertThat(search.getParameters().size()).isEqualTo(0);

		search.clearCriterias();
		LikeCriteria like4 = new LikeCriteria("", null, "imm", null, "", "mml", "", null);
		search.like("e.lastName", like4);
		assertThat(search.getCriterias().size()).isEqualTo(1);
		assertThat(search.getParameters().size()).isEqualTo(2);

		search.clearCriterias();
		LikeCriteria like5 = new LikeCriteria(LikeSearchType.BOTH, null, "imm", null, "", "mml", "", null);
		like5.setUpper(true);
		search.like("e.lastName", like5);

		assertThat(search.getCriterias().size()).isEqualTo(1);
		assertThat(search.getParameters().size()).isEqualTo(2);

	}

	@Test
	public void test() {
		JpaQlSearch search = new JpaQlSearch();
		search.from("Employee e");

		search.setDistinct(true);
		search.innerJoin("FETCH e.address");
		search.innerJoin("FETCH e.manager");
		search.innerJoin("FETCH e.phones");
		search.innerJoin("FETCH e.computers");
		search.innerJoin("FETCH e.projects");
		search.innerJoin("FETCH e.examinations");

		int cc = 0;
		int pc = 0;

		//string
		search.eq("e.firstName", employee.getFirstName());
		cc += 1;
		pc += 1;

		//integer
		search.eq("e.yearsOfService", employee.getYearsOfService());
		cc += 1;
		pc += 1;

		//dates
		search.eq("e.period.startDate", employee.getPeriod().getStartDate());
		cc += 1;
		pc += 1;
		search.eq("e.period.endDate", employee.getPeriod().getEndDate()); //null -> will not exist

		//long path
		search.eq("e.address.city", employee.getAddress().getCity());
		cc += 1;
		pc += 1;

		//etity
		search.eq("e.manager", employee.getManager());
		cc += 1;
		pc += 1;

		//2 values
		search.eq("e.addressId", employee.getAddress().getId(), employee.getAddress().getId());
		cc += 1;
		pc += 2;

		assertThat(search.getCriterias().size()).isEqualTo(cc);
		assertThat(search.getParameters().size()).isEqualTo(pc);

		search.eq("e.addressId", (String) null);
		search.eq("e.addressId", null, null);
		search.ne("e.addressId", -3);
		cc += 1;
		pc += 1;
		search.ne("e.addressId", -1, -2);
		cc += 1;
		pc += 2;
		search.ne("e.addressId", null, -1, null);
		cc += 1;
		pc += 1;
		search.eq("e.addressId", null, null, null);
		search.where("e.addressId", Operator.LOE, null);

		assertThat(search.getCriterias().size()).isEqualTo(cc);
		assertThat(search.getParameters().size()).isEqualTo(pc);

		search.eq("e.lastName", (String) null);
		search.eq("e.lastName", null, null);
		search.ne("e.lastName", "x");
		cc += 1;
		pc += 1;
		search.ne("e.lastName", "x", "y");
		cc += 1;
		pc += 2;
		search.ne("e.lastName", null, "x", null);
		cc += 1;
		pc += 1;
		search.where("e.lastName", Operator.LOE, null);

		assertThat(search.getCriterias().size()).isEqualTo(cc);
		assertThat(search.getParameters().size()).isEqualTo(pc);

		System.out.println(search.buildQl());
		List<Employee> list = search.getResultList(em);
		for (Employee employee : list) {
			System.out.println(employee);
		}
		assertThat(list.size()).isEqualTo(1);
		Employee e = list.get(0);
		System.out.println(e.getPhones());
		assertThat(e.getPhones().size()).isEqualTo(2);
		System.out.println(e.getProjects());
		assertThat(e.getProjects().size()).isEqualTo(2);
		System.out.println(e.getManager());
		System.out.println(e.getComputers());
		assertThat(e.getComputers().size()).isEqualTo(2);
		System.out.println(e.getExaminations());
		assertThat(e.getExaminations().size()).isEqualTo(2);
	}

}
