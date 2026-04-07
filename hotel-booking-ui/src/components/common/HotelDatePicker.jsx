import React, { useState } from 'react';
import { DayPicker } from 'react-day-picker';
import { format } from 'date-fns';
import 'react-day-picker/dist/style.css'; // Import default styles first

const HotelDatePicker = () => {
  const [range, setRange] = useState();

  // DISABLE PAST DATES:
  // The 'before: new Date()' matcher prevents selecting any day before today.
  const disabledDays = { before: new Date() };

  return (
    <div className="hotel-calendar-wrapper">
      <DayPicker
        mode="range"
        selected={range}
        onSelect={setRange}
        disabled={disabledDays} // apply the disabled matcher
        footer={
          range?.from ? (
            <p className="mt-4 text-sm text-gray-600">
              Selected from {format(range.from, 'PPP')}
              {range.to && ` to ${format(range.to, 'PPP')}`}
            </p>
          ) : (
            <p className="mt-4 text-sm text-gray-600">
              Please pick the first day.
            </p>
          )
        }
      />
    </div>
  );
};

export default HotelDatePicker;
