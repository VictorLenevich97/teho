package va.rit.teho.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.RepositoryTestRunner;
import va.rit.teho.entity.base.Base;
import va.rit.teho.repository.base.BaseRepository;

import javax.annotation.Resource;
import java.util.Optional;

@ContextConfiguration(classes = RepositoryTestRunner.class)
@DataJpaTest
public class BaseRepositoryTest {

    @Resource
    private BaseRepository baseRepository;

    @Test
    public void testAddBase() {
        Base b = new Base("short", "full");
        Base saved = baseRepository.save(b);

        Optional<Base> optionalBase = baseRepository.findById(saved.getId());
        Assertions.assertTrue(optionalBase.isPresent());
        Assertions.assertEquals(saved, optionalBase.get());
    }

    @Test
    public void testFindByFullName() {
        Base b = new Base("short", "full");
        Base saved = baseRepository.save(b);

        Optional<Base> optionalBase = baseRepository.findByFullName(b.getFullName());
        Assertions.assertTrue(optionalBase.isPresent());
        Assertions.assertEquals(saved, optionalBase.get());
    }

}
