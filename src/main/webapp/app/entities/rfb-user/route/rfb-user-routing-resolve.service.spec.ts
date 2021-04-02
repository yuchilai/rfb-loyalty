jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IRfbUser, RfbUser } from '../rfb-user.model';
import { RfbUserService } from '../service/rfb-user.service';

import { RfbUserRoutingResolveService } from './rfb-user-routing-resolve.service';

describe('Service Tests', () => {
  describe('RfbUser routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: RfbUserRoutingResolveService;
    let service: RfbUserService;
    let resultRfbUser: IRfbUser | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(RfbUserRoutingResolveService);
      service = TestBed.inject(RfbUserService);
      resultRfbUser = undefined;
    });

    describe('resolve', () => {
      it('should return IRfbUser returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRfbUser = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultRfbUser).toEqual({ id: 123 });
      });

      it('should return new IRfbUser if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRfbUser = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultRfbUser).toEqual(new RfbUser());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRfbUser = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultRfbUser).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
