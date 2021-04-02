import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IRfbEventAttendance, RfbEventAttendance } from '../rfb-event-attendance.model';
import { RfbEventAttendanceService } from '../service/rfb-event-attendance.service';
import { IRfbEvent } from 'app/entities/rfb-event/rfb-event.model';
import { RfbEventService } from 'app/entities/rfb-event/service/rfb-event.service';
import { IRfbUser } from 'app/entities/rfb-user/rfb-user.model';
import { RfbUserService } from 'app/entities/rfb-user/service/rfb-user.service';

@Component({
  selector: 'jhi-rfb-event-attendance-update',
  templateUrl: './rfb-event-attendance-update.component.html',
})
export class RfbEventAttendanceUpdateComponent implements OnInit {
  isSaving = false;

  rfbEventsSharedCollection: IRfbEvent[] = [];
  rfbUsersSharedCollection: IRfbUser[] = [];

  editForm = this.fb.group({
    id: [],
    attendanceDate: [],
    rfbEvent: [],
    rfbUser: [],
  });

  constructor(
    protected rfbEventAttendanceService: RfbEventAttendanceService,
    protected rfbEventService: RfbEventService,
    protected rfbUserService: RfbUserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rfbEventAttendance }) => {
      this.updateForm(rfbEventAttendance);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const rfbEventAttendance = this.createFromForm();
    if (rfbEventAttendance.id !== undefined) {
      this.subscribeToSaveResponse(this.rfbEventAttendanceService.update(rfbEventAttendance));
    } else {
      this.subscribeToSaveResponse(this.rfbEventAttendanceService.create(rfbEventAttendance));
    }
  }

  trackRfbEventById(index: number, item: IRfbEvent): number {
    return item.id!;
  }

  trackRfbUserById(index: number, item: IRfbUser): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRfbEventAttendance>>): void {
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

  protected updateForm(rfbEventAttendance: IRfbEventAttendance): void {
    this.editForm.patchValue({
      id: rfbEventAttendance.id,
      attendanceDate: rfbEventAttendance.attendanceDate,
      rfbEvent: rfbEventAttendance.rfbEvent,
      rfbUser: rfbEventAttendance.rfbUser,
    });

    this.rfbEventsSharedCollection = this.rfbEventService.addRfbEventToCollectionIfMissing(
      this.rfbEventsSharedCollection,
      rfbEventAttendance.rfbEvent
    );
    this.rfbUsersSharedCollection = this.rfbUserService.addRfbUserToCollectionIfMissing(
      this.rfbUsersSharedCollection,
      rfbEventAttendance.rfbUser
    );
  }

  protected loadRelationshipsOptions(): void {
    this.rfbEventService
      .query()
      .pipe(map((res: HttpResponse<IRfbEvent[]>) => res.body ?? []))
      .pipe(
        map((rfbEvents: IRfbEvent[]) =>
          this.rfbEventService.addRfbEventToCollectionIfMissing(rfbEvents, this.editForm.get('rfbEvent')!.value)
        )
      )
      .subscribe((rfbEvents: IRfbEvent[]) => (this.rfbEventsSharedCollection = rfbEvents));

    this.rfbUserService
      .query()
      .pipe(map((res: HttpResponse<IRfbUser[]>) => res.body ?? []))
      .pipe(
        map((rfbUsers: IRfbUser[]) => this.rfbUserService.addRfbUserToCollectionIfMissing(rfbUsers, this.editForm.get('rfbUser')!.value))
      )
      .subscribe((rfbUsers: IRfbUser[]) => (this.rfbUsersSharedCollection = rfbUsers));
  }

  protected createFromForm(): IRfbEventAttendance {
    return {
      ...new RfbEventAttendance(),
      id: this.editForm.get(['id'])!.value,
      attendanceDate: this.editForm.get(['attendanceDate'])!.value,
      rfbEvent: this.editForm.get(['rfbEvent'])!.value,
      rfbUser: this.editForm.get(['rfbUser'])!.value,
    };
  }
}
