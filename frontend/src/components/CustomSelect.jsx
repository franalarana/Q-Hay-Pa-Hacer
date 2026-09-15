import React, { useState, useRef, useEffect } from 'react';
import { ChevronDown } from 'lucide-react';
import * as styles from '../styles/customSelect.styles';

export default function CustomSelect({ value, onChange, options, placeholder = "Selecciona..." }) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const selectedOption = options.find(opt => opt.value === value);

  return (
    <div ref={dropdownRef} style={styles.wrapper}>
      <div
        className={`pastel-input ${isOpen ? 'open' : ''}`}
        style={styles.getTrigger(isOpen)}
        onClick={() => setIsOpen(!isOpen)}
      >
        <span style={styles.getLabel(selectedOption)}>
          {selectedOption ? selectedOption.label : placeholder}
        </span>
        <ChevronDown size={16} color="var(--text-muted)" style={styles.getChevron(isOpen)} />
      </div>

      {isOpen && (
        <div style={styles.dropdown} className="custom-scrollbar">
          {options.map((opt, idx) => (
            <div
              key={idx}
              onClick={() => {
                onChange(opt.value);
                setIsOpen(false);
              }}
              className="custom-select-option"
              style={styles.getOption(opt, value)}
            >
              {opt.label}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
