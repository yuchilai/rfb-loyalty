jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { RfbUserService } from '../service/rfb-user.service';
import { IRfbUser, RfbUser } from '../rfb-user.model';
import { IRfbLocation } from 'app/entities/rfb-location/rfb-location.model';
import { RfbLocationService } from 'app/entities/rfb-location/service/rfb-location.service';

import { RfbUserUpdateComponent } from './rfb-user-update.component';

describe('Component Tests', () => {
  describe('RfbUser Management Update Component', () => {
    let comp: RfbUserUpdateComponent;
    let fixture: ComponentFixture<RfbUserUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let rfbUserService: RfbUserService;
    let rfbLocationService: RfbLocationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [RfbUserUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(RfbUserUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RfbUserUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      rfbUserService = TestBed.inject(RfbUserService);
      rfbLocationService = TestBed.inject(RfbLocationService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call homeLocation query and add missing value', () => {
        const rfbUser: IRfbUser = { id: 456 };
        const homeLocation: IRfbLocation = { id: 45057 };
        rfbUser.homeLocation = homeLocation;

        const homeLocationCollection: IRfbLocation[] = [{ id: 23256 }];
        spyOn(rfbLocationService, 'query').and.returnValue(of(new HttpResponse({ body: homeLocationCollection })));
        const expectedCollection: IRfbLocation[] = [homeLocation, ...homeLocationCollection];
        spyOn(rfbLocationService, 'addRfbLocationToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ rfbUser });
        comp.ngOnInit();

        expect(rfbLocationService.query).toHaveBeenCalled();
        expect(rfbLocationService.addRfbLocationToCollectionIfMissing).toHaveBeenCalledWith(homeLocationCollection, homeLocation);
        expect(comp.homeLocationsCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const rfbUser: IRfbUser = { id: 456 };
        const homeLocation: IRfbLocation = { id: 5205 };
        rfbUser.homeLocation = homeLocation;

        activatedRoute.data = of({ rfbUser });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(rfbUser));
        expect(comp.homeLocationsCollection).toContain(homeLocation);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbUser = { id: 123 };
        spyOn(rfbUserService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: rfbUser }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(rfbUserService.update).toHaveBeenCalledWith(rfbUser);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbUser = new RfbUser();
        spyOn(rfbUserService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: rfbUser }));
        saveSubject.complete();

        // THEN
        expect(rfbUserService.create).toHaveBeenCalledWith(rfbUser);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbUser = { id: 123 };
        spyOn(rfbUserService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(rfbUserService.update).toHaveBeenCalledWith(rfbUser);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackRfbLocationById', () => {
        it('Should return tracked RfbLocation primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackRfbLocationById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
