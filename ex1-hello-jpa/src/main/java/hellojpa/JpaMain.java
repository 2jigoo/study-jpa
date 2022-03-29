package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
//            selectList(em);
//            insertAndCache(em);
//            equalsWhenPkIsSame(em);
//            sendSQLWhenCommit(em);

            Member member = em.find(Member.class, 150L);
            member.setName("이멤버");
            // 1. em.persist() 해야할 것 같지만, 엔티티의 변경이 감지되어 update 쿼리가 나간다. (dirty check)

            em.detach(member);
            // 2. 준영속 상태로 만들면, 영속성 컨텍스트에서 관리되지 않으므로 변경감지가 되지 않는다.

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();; // 내부적으로 DB 커넥션을 얻어온다.
        }

        emf.close(); // application 종료시
    }

    private static void sendSQLWhenCommit(EntityManager em) {
        Member member1 = new Member(150L, "A");
        Member member2 = new Member(160L, "B");

        em.persist(member1);
        em.persist(member2);

        System.out.println("============================");
        // ===== 이후에 쿼리가 찍힌다
        // 영속성 컨텍스트에 엔티티와 쿼리를 쌓아두다가 커밋하는 시점에 DB에 쿼리 보냄.
    }

    private static void equalsWhenPkIsSame(EntityManager em) {
        Member findMember1 = em.find(Member.class, 100L);
        Member findMember2 = em.find(Member.class, 100L);

        System.out.println("findMember == findMember2 = " + (findMember1 == findMember2));
    }

    private static void insertAndCache(EntityManager em) {
        Member member = new Member();
        member.setId(100L);
        member.setName("Hello JPA");

        System.out.println("=== BEFORE ===");
        em.persist(member);
        System.out.println("=== AFTER ===");

        Member findMember = em.find(Member.class, 100L);

        System.out.println("findMember.id = " + findMember.getId());
        System.out.println("findMember.name = " + findMember.getName());
    }

    private static void selectList(EntityManager em) {
        Member findMember = em.find(Member.class, 1L);
        findMember.setName("이맘바");

        List<Member> members = em.createQuery("select m from Member as m", Member.class)
                .setFirstResult(5)
                .setMaxResults(8)
                .getResultList();

        for (Member member : members) {
            System.out.println("member.name: " + member.getName());
        }
    }
}
