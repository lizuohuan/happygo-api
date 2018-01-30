package com.magicbeans.happygo;


import com.alibaba.fastjson.JSON;
import com.magicbeans.base.Pages;
import com.magicbeans.base.db.Filter;
import com.magicbeans.base.db.Order;
import com.magicbeans.happygo.entity.Member;
import com.magicbeans.happygo.service.IMemberService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest
@WebAppConfiguration
public class MemberServiceTest {


    @Autowired
    private IMemberService memberService;


    @Test
    public void saveTest() {
        Member member = new Member();
        member.setName("asdasdadasd");
        member.setStatus(Member.Status.DISABLE);
        memberService.save(member);
    }

    @Test
    public void findMoreProperties() {

        Member member = memberService.find(new String[]{"id", "name"}, new Object[]{"912608300427337730", "5"});

        Assert.assertNotNull(member);
    }

    @Test
    public void findAllTest() {
        List<Member> list = memberService.findAll();
        System.out.println(JSON.toJSON(list));
    }

    @Test
    public void selectCountTest() {
        System.out.println(memberService.selectCount());
    }


    @Test
    public void findListTest() {
        System.out.println(memberService.findList("status", "1"));
    }

    @Test
    public void findListMorePropertiesTest() {
        System.out.println(memberService.findList(new String[]{"status", "name"}, new Object[]{"1", "1"}));
    }

    @Test
    public void findListFilter() {
        Filter[] filters = new Filter[]{
                Filter.eq("status", "1"),
                Filter.eq("name", 1)
        };
        System.out.println(memberService.findList(Arrays.asList(filters), Order.desc("name")));
    }

    @Test
    public void findListLimitFilter() {
        Filter[] filters = new Filter[]{
                Filter.eq("status", "1"),
                Filter.eq("name", 1)
        };
        System.out.println(JSON.toJSON(memberService.findList(1, 2, Arrays.asList(filters), Order.desc("name"))));
    }

    @Test
    public void findListLimitFilterOrders() {
        Filter[] filters = new Filter[]{
                Filter.eq("status", "1"),
                Filter.eq("name", 1)
        };
        Order[] orders = new Order[]{
                Order.desc("name"),
                Order.desc("status")
        };
        System.out.println(JSON.toJSON(memberService.findList(0, 2, Arrays.asList(filters), Arrays.asList(orders))));
    }

    @Test
    public void findPageTest() {
        Filter[] filters = new Filter[]{
                Filter.eq("status", "1"),
                Filter.eq("name", 1)
        };
        Order[] orders = new Order[]{
                Order.desc("name"),
                Order.desc("status")
        };
        Pages<Member> page = new Pages<>(0, 10);
        memberService.findPage(page, null, null);
        System.out.println(JSON.toJSON(page));
    }


    @Test
    public void orTests() {
//        Filter[] infilters = new Filter[]{
//                Filter.in("status",new Object[]{1,2})
//        };

//        Filter[] orfilters = new Filter[]{
//                Filter.eq("id","111"),
//                Filter.or(Filter.eq("status",1)),
//                Filter.like("name","1")
//
//        };

        Filter[] betweenFilters = new Filter[]{
                Filter.between("create_time", "2017-10-12 16:52:29", "2017-10-13 16:52:29")
        };

        Pages<Member> page = new Pages<>(0, 10);
        page = memberService.findPage(page, Arrays.asList(betweenFilters), null);
        System.out.println("");
    }


    @Test
    public void localPage() {
        Pages pages =    new Pages();
        pages.setSize(1);
        Pages<Member> memberPages =  memberService.selectPage(pages);
        System.out.println("");
    }
}
