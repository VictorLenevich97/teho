package va.rit.teho.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.RepositoryTestRunner;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.repository.formation.FormationRepository;

import javax.annotation.Resource;
import java.util.Optional;

@ContextConfiguration(classes = RepositoryTestRunner.class)
@DataJpaTest
public class FormationRepositoryTest {

    @Resource
    private FormationRepository formationRepository;

    @Test
    public void testAddBase() {
        Formation b = new Formation(1L, "short", "full");
        Formation saved = formationRepository.save(b);

        Optional<Formation> optionalBase = formationRepository.findById(saved.getId());
        Assertions.assertTrue(optionalBase.isPresent());
        Assertions.assertEquals(saved, optionalBase.get());
    }

    @Test
    public void testFindByFullName() {
        Formation b = new Formation(1L, "short", "full");
        Formation saved = formationRepository.save(b);

        Optional<Formation> optionalBase = formationRepository.findByFullName(b.getFullName());
        Assertions.assertTrue(optionalBase.isPresent());
        Assertions.assertEquals(saved, optionalBase.get());
    }

}
