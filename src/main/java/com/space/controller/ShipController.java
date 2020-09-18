package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class ShipController {

    private ShipService shipService;

    @Autowired
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    // GET SHIPS LIST
    @GetMapping("/ships")
    public List<Ship> getShipsList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Specification<Ship> specification = Specification.where(
                shipService.filterByName(name)
                .and(shipService.filterByPlanet(planet))
                .and(shipService.filterByShipType(shipType))
                .and(shipService.filterByProdDate(after, before))
                .and(shipService.filterByIsUsed(isUsed))
                .and(shipService.filterBySpeed(minSpeed, maxSpeed))
                .and(shipService.filterByCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.filterByRating (minRating, maxRating)));
        return shipService.findAll(specification, pageable).getContent();
    }

    // GET SHIPS COUNT
    @GetMapping("/ships/count")
    public Integer getShipsCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating)
    {
        Specification<Ship> specification = Specification.where(shipService.filterByName(name)
                .and(shipService.filterByPlanet(planet))
                .and(shipService.filterByShipType(shipType))
                .and(shipService.filterByProdDate(after, before))
                .and(shipService.filterByIsUsed(isUsed))
                .and(shipService.filterBySpeed(minSpeed, maxSpeed))
                .and(shipService.filterByCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.filterByRating (minRating, maxRating)));
        return shipService.findAll(specification).size();
    }

    // CREATE SHIP
    @PostMapping("/ships")
    public Ship createShip(@RequestBody Ship ship){
        return shipService.createShip(ship);
    }

    // GET SHIP
    @GetMapping("/ships/{id}")
    public Ship getShip(@PathVariable Long id){
        return shipService.getById(id);
    }

    // UPDATE SHIP
    @PostMapping("/ships/{id}")
    public Ship updateShip(@PathVariable Long id, @RequestBody Ship ship){
        return shipService.updateById(id, ship);
    }

    // DELETE SHIP
    @DeleteMapping("/ships/{id}")
    public void deleteShip(@PathVariable Long id){
        shipService.deleteById(id);
    }
}