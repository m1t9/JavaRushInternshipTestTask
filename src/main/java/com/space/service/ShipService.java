package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShipService {

    private ShipsRepository shipsRepository;

    @Autowired
    public void setShipsRepository(ShipsRepository shipsRepository) {
        this.shipsRepository = shipsRepository;
    }

    public Ship createShip(Ship ship) {
        if (!checkShip(ship)) throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Bad ship");
        if (ship.getUsed() == null) ship.setUsed(false);
        ship.setRating(ship.rating());
        return shipsRepository.saveAndFlush(ship);
    }

    public Ship getById(Long id) {
        if (id <= 0) throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Bad ID");
        Optional<Ship> optionalShip = shipsRepository.findById(id);
        return optionalShip.orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,"Ship not found"));
    }

    public Ship updateById(Long id, Ship ship) {

        Ship localShip = getById(id);

        // update name
        if (ship.getName() != null && !ship.getName().equals("") && ship.getName().length() <= 50) {
            localShip.setName(ship.getName());
        } else if (ship.getName() != null && ship.getName().equals("")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad ship name");
        }

        // update planet
        if (ship.getPlanet() != null && !ship.getPlanet().equals("") && ship.getPlanet().length() <= 50) {
            localShip.setPlanet(ship.getPlanet());
        } else if (ship.getPlanet() != null && ship.getPlanet().equals("")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad planet name");
        }

        // update type
        if (ship.getShipType() != null &&
                (ship.getShipType() == ShipType.TRANSPORT ||
                        ship.getShipType() == ShipType.MILITARY ||
                        ship.getShipType() == ShipType.MERCHANT)) {
            localShip.setShipType(ship.getShipType());
        } else if (ship.getShipType() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad ship type");
        }

        // update prodDate
        if (ship.getProdDate() != null &&
                ship.getProdDate().getTime() >= 26192246400000L &&
                ship.getProdDate().getTime() <= 33103209600000L) {
            localShip.setProdDate(ship.getProdDate());
        } else if (ship.getProdDate() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad prod date");
        }

        // update speed
        if (ship.getSpeed() != null && ship.getSpeed() >= 0.01 && ship.getSpeed() <= 0.99) {
            localShip.setSpeed(ship.getSpeed());
        } else if (ship.getSpeed() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad speed");
        }

        // update crewSize
        if (ship.getCrewSize() != null && ship.getCrewSize() >= 1 && ship.getCrewSize() <= 9999) {
            localShip.setCrewSize(ship.getCrewSize());
        } else if (ship.getCrewSize() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad crew size");
        }

        localShip.setRating(localShip.rating());

        return shipsRepository.saveAndFlush(localShip);
    }

    public void deleteById(Long id) {
        getById(id);
        shipsRepository.deleteById(id);
    }

    public Specification<Ship> filterByName(String name) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (name == null) return null;
                return criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
        };
    }

    public Specification<Ship> filterByPlanet(String planet) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (planet == null) return null;
                return criteriaBuilder.like(root.get("planet"), "%" + planet + "%");
            }
        };
    }

    public Specification<Ship> filterByShipType(ShipType shipType) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (shipType == null) return null;
                return criteriaBuilder.equal(root.get("shipType"), shipType);
            }
        };
    }

    public Specification<Ship> filterByProdDate(Long minDate, Long maxDate) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minDate == null && maxDate == null) return null;
                if (minDate == null) return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"),new Date(maxDate));
                if (maxDate == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"),new Date(minDate));
                return criteriaBuilder.between(root.get("prodDate"), new Date(minDate), new Date(maxDate));
            }
        };
    }

    public Specification<Ship> filterByIsUsed(Boolean isUsed) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (isUsed == null) return null;
                if (isUsed) return criteriaBuilder.isTrue(root.get("isUsed"));
                return criteriaBuilder.isFalse(root.get("isUsed"));
            }
        };
    }

    public Specification<Ship> filterBySpeed(Double minSpeed, Double maxSpeed) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minSpeed == null && maxSpeed == null) return null;
                if (minSpeed == null) return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed);
                if (maxSpeed == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed);
                return criteriaBuilder.between(root.get("speed"), minSpeed, maxSpeed);
            }
        };
    }

    public Specification<Ship> filterByCrewSize(Integer minCrewSize, Integer maxCrewSize) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minCrewSize == null && maxCrewSize == null) return null;
                if (minCrewSize == null) return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrewSize);
                if (maxCrewSize == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrewSize);
                return criteriaBuilder.between(root.get("crewSize"), minCrewSize, maxCrewSize);
            }
        };
    }

    public Specification<Ship> filterByRating(Double minRating, Double maxRating) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minRating == null && maxRating == null) return null;
                if (minRating == null) return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating);
                if (maxRating == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating);
                return criteriaBuilder.between(root.get("rating"), minRating, maxRating);
            }
        };
    }

    public Page<Ship> findAll(Specification<Ship> specification, Pageable pageable) {
        return shipsRepository.findAll(specification, pageable);
    }

    public List<Ship> findAll(Specification<Ship> specification) {
        return shipsRepository.findAll(specification);
    }

    private boolean checkShip(Ship ship) {
        return (ship.getName() != null && !ship.getName().equals("") && ship.getName().length() <= 50) &&
                (ship.getPlanet() != null && !ship.getPlanet().equals("") && ship.getPlanet().length() <= 50) &&
                (ship.getShipType() != null &&
                        (ship.getShipType() == ShipType.TRANSPORT ||
                                ship.getShipType() == ShipType.MILITARY ||
                                ship.getShipType() == ShipType.MERCHANT)) &&
                (ship.getProdDate() != null &&
                        ship.getProdDate().getTime() >= 26192246400000L &&
                        ship.getProdDate().getTime() <= 33103209600000L) &&
                (ship.getSpeed() != null && ship.getSpeed() >= 0.01 && ship.getSpeed() <= 0.99) &&
                (ship.getCrewSize() != null && ship.getCrewSize() >= 1 && ship.getCrewSize() <= 9999);
    }
}
