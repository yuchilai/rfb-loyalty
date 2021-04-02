import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { RfbUserService } from '../service/rfb-user.service';

import { RfbUserComponent } from './rfb-user.component';

describe('Component Tests', () => {
  describe('RfbUser Management Component', () => {
    let comp: RfbUserComponent;
    let fixture: ComponentFixture<RfbUserComponent>;
    let service: RfbUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [RfbUserComponent],
      })
        .overrideTemplate(RfbUserComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RfbUserComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(RfbUserService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.rfbUsers?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
