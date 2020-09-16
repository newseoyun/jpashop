package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    // 아래는 엔티티와 서비스가 서로 연결된 약간의 통합 테스트인데
    // 비즈니스 메소드 등의 단위 테스트가 더 좋은 테스트다.

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception  {

        //given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, book.getStockQuantity());

    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception  {

        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 100;

        //when
        orderService.order(member.getId(), item.getId(), orderCount);

        //then
        fail("재고 수량 부족 예외가 발생해야 한다.");
    }

    @Test
    public void 주문취소() throws Exception  {

        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소 시 상태는 CANCEL", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("재고를 회복해야한다.", 10, item.getStockQuantity());
    }


    /* Ctrl Alt M : 메소드로 추출*/
    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name); /* Ctrl Alt P : 파라미터화 */
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
    
}