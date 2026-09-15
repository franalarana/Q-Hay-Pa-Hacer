import React, { useState, useRef, useEffect } from 'react';
import { ChevronDown } from 'lucide-react';
import '../styles/customSelect.css';

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
    <div ref={dropdownRef} className="select-wrapper">
      <div
        className={`pastel-input select-trigger ${isOpen ? 'open' : ''}`}
        onClick={() => setIsOpen(!isOpen)}
      >
        <span className={`select-label ${selectedOption ? 'has-value' : ''}`}>
          {selectedOption ? selectedOption.label : placeholder}
        </span>
        <ChevronDown size={16} color="var(--text-muted)" className={`select-chevron ${isOpen ? 'open' : ''}`} />
      </div>

      {isOpen && (
        <div className="select-dropdown custom-scrollbar">
          {options.map((opt, idx) => (
            <div
              key={idx}
              onClick={() => {
                onChange(opt.value);
                setIsOpen(false);
              }}
              className={`custom-select-option select-option ${opt.value === value ? 'selected' : ''} ${opt.isDivider ? 'divider' : ''}`}
            >
              {opt.label}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
