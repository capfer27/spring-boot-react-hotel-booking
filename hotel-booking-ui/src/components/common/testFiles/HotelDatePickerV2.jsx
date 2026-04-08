import { DayPicker } from 'react-day-picker';
import { useState } from 'react';

export const HotelDatePickerV2 = ({ onRangeSelect }) => {
  const [range, setRange] = useState();

  const handleSelect = (newRange) => {
    setRange(newRange);
    if (onRangeSelect) onRangeSelect(newRange); // Send back to parent
  };

  return (
    <DayPicker
      mode="range"
      selected={range}
      onSelect={handleSelect}
      disabled={{ before: new Date() }}
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
  );
};
