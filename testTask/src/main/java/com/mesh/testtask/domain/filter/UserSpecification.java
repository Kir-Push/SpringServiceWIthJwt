package com.mesh.testtask.domain.filter;

import com.mesh.testtask.domain.entity.Phone;
import com.mesh.testtask.domain.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class UserSpecification implements Specification<User> {

    private FilterCriteria criteria;

    @Override
    public Specification<User> and(Specification<User> other) {
        return Specification.super.and(other);
    }

    @Override
    public Specification<User> or(Specification<User> other) {
        return Specification.super.or(other);
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Path<String> key = null;
        if (criteria.getKey().contains("phone")) {
            Join<User, Phone> groupJoin = root.join("phones");
            key = groupJoin.get("value");
        } else {
           key = root.get(criteria.getKey());
        }
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
                    key, criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
                    key, criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase("like")) {
            return builder.like(
                    key, "%" + criteria.getValue() + "%");
        } else if (criteria.getOperation().equalsIgnoreCase("equals")) {
            return builder.equal(key, criteria.getValue());
        }
        return null;
    }
}
