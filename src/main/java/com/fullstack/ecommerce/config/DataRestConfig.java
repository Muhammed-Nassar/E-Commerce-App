package com.fullstack.ecommerce.config;

import com.fullstack.ecommerce.entity.Country;
import com.fullstack.ecommerce.entity.Product;
import com.fullstack.ecommerce.entity.ProductCategory;
import com.fullstack.ecommerce.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class DataRestConfig implements RepositoryRestConfigurer {
    private EntityManager entityManager;

    @Autowired
    public DataRestConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        HttpMethod[] unSupportedActions = {HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE};

        // To prevent (post,put,delete) Methods on products
        disableHttpMethods(Product.class, config, unSupportedActions);

        // To prevent (post,put,delete) Methods on product-category
        disableHttpMethods(ProductCategory.class, config, unSupportedActions);

        // To prevent (post,put,delete) Methods on country
        disableHttpMethods(Country.class, config, unSupportedActions);

        // To prevent (post,put,delete) Methods on state
        disableHttpMethods(State.class, config, unSupportedActions);

        //call internal helper method
        exposeIds(config);
    }

    private void disableHttpMethods(Class classType, RepositoryRestConfiguration config, HttpMethod[] unSupportedActions) {
        config.getExposureConfiguration()
                .forDomainType(classType)
                .withItemExposure((metData, httpMethods) -> httpMethods.disable(unSupportedActions))
                .withCollectionExposure((metData, httpMethods) -> httpMethods.disable(unSupportedActions));
    }

    // Spring Data Rest don't expose entities ids to Jason object
    private void exposeIds(RepositoryRestConfiguration config) {


        // get list of  entity classes from EntityManager
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        // Put them in Array of entity classes
        List<Class> entityClassesList = new ArrayList<>();

        for (EntityType tempEntityType : entities) {
            entityClassesList.add(tempEntityType.getJavaType());
        }
        // expose the entity  ids for  array of  entity/domain types
        Class[] domainTypes = entityClassesList.toArray(new Class[0]);
        config.exposeIdsFor(domainTypes);


    }
}
