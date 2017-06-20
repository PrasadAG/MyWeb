package ua.com.alexcoffee.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.alexcoffee.exception.BadRequestException;
import ua.com.alexcoffee.exception.WrongInformationException;
import ua.com.alexcoffee.model.Product;
import ua.com.alexcoffee.repository.ProductRepository;
import ua.com.alexcoffee.service.interfaces.ProductService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Класс сервисного слоя реализует методы доступа объектов класса {@link Product}
 * в базе данных интерфейса {@link ProductService}, наследует родительский
 * класс {@link MainServiceImpl}, в котором реализованы основные методы.
 * Класс помечан аннотацией @Service - аннотация обьявляющая, что этот класс представляет
 * собой сервис – компонент сервис-слоя. Сервис является подтипом класса @Component.
 * Использование данной аннотации позволит искать бины-сервисы автоматически.
 * Методы класса помечены аннотацией @Transactional - перед исполнением метода помеченного
 * данной аннотацией начинается транзакция, после выполнения метода транзакция коммитится,
 * при выбрасывании RuntimeException откатывается.
 *
 * @author Yurii Salimov (yuriy.alex.salimov@gmail.com)
 * @version 1.2
 * @see MainServiceImpl
 * @see ProductService
 * @see ProductRepository
 * @see Product
 */
@Service
@ComponentScan(basePackages = "ua.com.alexcoffee.repository")
public final class ProductServiceImpl extends MainServiceImpl<Product> implements ProductService {
    /**
     * Реализация интерфейса {@link ProductRepository}
     * для работы с товаров базой данных.
     */
    private final ProductRepository repository;

    /**
     * Конструктор для инициализации основных переменных сервиса.
     * Помечаный аннотацией @Autowired, которая позволит Spring
     * автоматически инициализировать объект.
     *
     * @param repository Реализация интерфейса {@link ProductRepository}
     *                          для работы с товаров базой данных.
     */
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public ProductServiceImpl(final ProductRepository repository) {
        super(repository);
        this.repository = repository;
    }

    /**
     * Возвращает товар, у которого совпадает параметр url. Режим только для чтения.
     *
     * @param url URL товара для возврата.
     * @return Объект класса {@link Product} - товара с уникальным url полем.
     * @throws WrongInformationException Бросает исключение,
     *                                   если пустой входной параметр url.
     * @throws BadRequestException       Бросает исключение,
     *                                   если не найден товар с входящим параметром url.
     */
    @Override
    @Transactional(readOnly = true)
    public Product getByUrl(final String url) throws WrongInformationException, BadRequestException {
        if (isBlank(url)) {
            throw new WrongInformationException("No product URL!");
        }
        final Product product = this.repository.findByUrl(url);
        if (product == null) {
            throw new BadRequestException("Can't find product by url " + url + "!");
        }
        return product;
    }

    /**
     * Возвращает товар, у которого совпадает уникальный
     * артикль с значением входящего параметра. Режим только для чтения.
     *
     * @param article Артикль товара для возврата.
     * @return Объект класса {@link Product} - товара с уникальным артиклем.
     * @throws BadRequestException Бросает исключение, если не найден
     *                             товар с входящим параметром article.
     */
    @Override
    @Transactional(readOnly = true)
    public Product getByArticle(final int article)
            throws BadRequestException {
        final Product product = this.repository.findByArticle(article);
        if (product == null) {
            throw new BadRequestException("Can't find product by article " + article + "!");
        }
        return product;
    }

    /**
     * Возвращает список товаров, которые относятся к категории
     * с уникальным URL - входным параметром.
     * Режим только для чтения.
     *
     * @param url URL категории, товары которой будут возвращены.
     * @return Объект типа {@link List} - список товаров.
     * @throws WrongInformationException Бросает исключение,
     *                                   если пустой входной параметр url.
     * @throws BadRequestException       Бросает исключение,
     *                                   если не найдена категория с входящим параметром url.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getByCategoryUrl(final String url)
            throws WrongInformationException, BadRequestException {
        if (isBlank(url)) {
            throw new WrongInformationException("No category URL!");
        }
        return this.repository.findByCategoryUrl(url);
    }

    /**
     * Возвращает список товаров, которые относятся к категории
     * с уникальным кодом id - входным параметром.
     * Режим только для чтения.
     *
     * @param id Код категории, товары которой будут возвращены.
     * @return Объект типа {@link List} - список товаров.
     * @throws WrongInformationException Бросает исключение,
     *                                   если пустой входной параметр id.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getByCategoryId(final long id) throws WrongInformationException {
        return this.repository.findByCategoryId(id);
    }

    /**
     * Возвращает список рандомных товаров, которые относятся к категории
     * с уникальным кодом id - входным параметром.
     *
     * @param size Количество товаров в списке.
     * @param id   Код категории, товары которой будут возвращены.
     * @return Объект типа {@link List} - список товаров.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getRandomByCategoryId(final int size, final long id) {
        return getRandomByCategoryId(size, id, -1L);
    }

    /**
     * Возвращает список рандомных товаров, которые относятся к категории
     * с уникальным кодом id - входным параметром.
     * Режим только для чтения.
     *
     * @param size               Количество товаров в списке.
     * @param categoryId         Код категории, товары которой будут возвращены.
     * @param differentProductId Код товара, который точно не будет включен в список.
     * @return Объект типа {@link List} - список товаров.
     * @throws WrongInformationException Бросает исключение,
     *                                   если несли пустой хотя бы одис с параметров.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getRandomByCategoryId(
            final int size,
            final long categoryId,
            final long differentProductId
    ) throws WrongInformationException {
        final List<Product> products = this.repository.findByCategoryId(categoryId);
        if (products.isEmpty()) {
            return new ArrayList<>();
        }
        products.remove(this.repository.findOne(differentProductId));
        return getShuffleSubList(products, 0, size);
    }

    /**
     * Возвращает список рандомных товаров.
     * Режим только для чтения.
     *
     * @param size Количество товаров в списке.
     * @return Объект типа {@link List} - список товаров.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getRandom(final int size) {
        final List<Product> products = this.repository.findAll();
        if (products.isEmpty()) {
            return new ArrayList<>();
        }
        return getShuffleSubList(products, 0, size);
    }

    /**
     * Удаляет товар, у которого совпадает параметр url.
     *
     * @param url URL товара для удаления.
     * @throws WrongInformationException Бросает исключение,
     *                                   если пустой входной параметр url.
     */
    @Override
    @Transactional
    public void removeByUrl(final String url) throws WrongInformationException {
        if (isBlank(url)) {
            throw new WrongInformationException("No product URL!");
        }
        this.repository.deleteByUrl(url);
    }

    /**
     * Удаляет товар, у которого совпадает параметр article.
     *
     * @param article артикль товара для удаления.
     */
    @Override
    @Transactional
    public void removeByArticle(final int article) {
        this.repository.deleteByArticle(article);
    }

    /**
     * Удаляет товары, которые пренадлежат категории
     * с уникальным URL - входным параметром.
     *
     * @param url URL категории, товары которой будут удалены.
     * @throws WrongInformationException Бросает исключение,
     *                                   если пустой входной параметр url.
     * @throws BadRequestException       Бросает исключение,
     *                                   если не найдена категория с входящим параметром url.
     */
    @Override
    @Transactional
    public void removeByCategoryUrl(final String url)
            throws WrongInformationException, BadRequestException {
        if (isBlank(url)) {
            throw new WrongInformationException("No category URL!");
        }
        this.repository.deleteByCategoryUrl(url);
    }

    /**
     * Удаляет товары, которые пренадлежат категории
     * с уникальным кодом - входным параметром.
     *
     * @param id Код категории, товары котрой будут удалены.
     * @throws BadRequestException Бросает исключение,
     *                             если не найдена категория с входящим параметром id.
     */
    @Override
    @Transactional
    public void removeByCategoryId(final long id) throws BadRequestException {
        this.repository.deleteByCategoryId(id);
    }

    /**
     * Возвращает список перемешаных товаров
     * начиная с позиции start и заканчиваю позицеей end.
     *
     * @param products Список товаров для обработки.
     * @param start    Начальная позиция выборки товаров из списка.
     * @param end      Конечная позиция выборки товаров из списка.
     * @return Объект типа {@link List} -список перемешаных товаров
     * или пустой лист.
     */
    private static List<Product> getShuffleSubList(
            final List<Product> products,
            final int start,
            final int end
    ) {
        if ((products == null) || (products.isEmpty()) ||
                (start > products.size()) ||
                (start > end) || (start < 0) || (end < 0)) {
            return new ArrayList<>();
        }
        Collections.shuffle(products);
        return products.subList(
                start,
                end <= products.size() ? end : products.size()
        );
    }
}
