import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IRfbUser, RfbUser } from '../rfb-user.model';
import { RfbUserService } from '../service/rfb-user.service';
import { IRfbLocation } from 'app/entities/rfb-location/rfb-location.model';
import { RfbLocationService } from 'app/entities/rfb-location/service/rfb-location.service';

@Component({
  selector: 'jhi-rfb-user-update',
  templateUrl: './rfb-user-update.component.html',
})
export class RfbUserUpdateComponent implements OnInit {
  isSaving = false;

  homeLocationsCollection: IRfbLocation[] = [];

  editForm = this.fb.group({
    id: [],
    username: [],
    homeLocation: [],
  });

  constructor(
    protected rfbUserService: RfbUserService,
    protected rfbLocationService: RfbLocationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rfbUser }) => {
      this.updateForm(rfbUser);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const rfbUser = this.createFromForm();
    if (rfbUser.id !== undefined) {
      this.subscribeToSaveResponse(this.rfbUserService.update(rfbUser));
    } else {
      this.subscribeToSaveResponse(this.rfbUserService.create(rfbUser));
    }
  }

  trackRfbLocationById(index: number, item: IRfbLocation): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRfbUser>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(rfbUser: IRfbUser): void {
    this.editForm.patchValue({
      id: rfbUser.id,
      username: rfbUser.username,
      homeLocation: rfbUser.homeLocation,
    });

    this.homeLocationsCollection = this.rfbLocationService.addRfbLocationToCollectionIfMissing(
      this.homeLocationsCollection,
      rfbUser.homeLocation
    );
  }

  protected loadRelationshipsOptions(): void {
    this.rfbLocationService
      .query({ filter: 'rfbuser-is-null' })
      .pipe(map((res: HttpResponse<IRfbLocation[]>) => res.body ?? []))
      .pipe(
        map((rfbLocations: IRfbLocation[]) =>
          this.rfbLocationService.addRfbLocationToCollectionIfMissing(rfbLocations, this.editForm.get('homeLocation')!.value)
        )
      )
      .subscribe((rfbLocations: IRfbLocation[]) => (this.homeLocationsCollection = rfbLocations));
  }

  protected createFromForm(): IRfbUser {
    return {
      ...new RfbUser(),
      id: this.editForm.get(['id'])!.value,
      username: this.editForm.get(['username'])!.value,
      homeLocation: this.editForm.get(['homeLocation'])!.value,
    };
  }
}
