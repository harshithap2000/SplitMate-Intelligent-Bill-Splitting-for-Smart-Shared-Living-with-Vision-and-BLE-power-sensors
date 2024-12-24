# SplitMate Gamma: Intelligent Bill-Splitting for Shared Living

SplitMate Gamma is a smart application designed to streamline bill-splitting in shared living environments. By integrating IoT technologies such as BLE power sensors and security cameras, the app enables accurate and fair expense tracking based on individual usage.

## Features

- **Usage Tracking:**
  - Real-time monitoring of utility usage using IoT devices such as BLE power sensors and security cameras.
  - Integration of tenant-specific data via face/body recognition and beaconing from smartphones.

- **Bill Management:**
  - Automated computation of utility costs based on actual usage data.
  - Support for detailed bill breakdowns and notifications for payment due dates.

- **Tenant Management:**
  - A principal tenant dashboard for managing user access, adding/removing tenants, and monitoring usage.
  - Request and authorization system for tenants to access utilities.

- **Backup Mechanisms:**
  - Offline functionality with in-house voice alerts for unauthorized access when the internet is unavailable.

- **Scalability and Accessibility:**
  - Designed to handle multiple users and houses with features adhering to WCAG 2.1 standards for accessibility.

## Technology Stack

- **Frontend:** Android Studio with Jetpack Compose for responsive, declarative UI development.
- **Backend:** Cloud-based server for scalable data processing and bill calculations.
- **Hardware:** Raspberry Pi and BLE-enabled sensors for usage tracking.

## Screens

1. **Welcome and Authentication:**
   - Clean and simple login/signup screens with role-based options (principal or regular tenant).

2. **Dashboard:**
   - Role-specific dashboards for monitoring bills, tenants, and utility usage.

3. **Bill Management:**
   - Monthly bill summaries with usage graphs and quick payment options.

4. **Tenant and Utility Management:**
   - Easy management of tenants and utilities with options for editing and access control.

5. **Profile Management:**
   - Displays user details, roles, and associated houses.

## Implementation Details

- **Development Approach:**
  - Wireframing in Figma for collaborative design and workflow planning.
  - Use of Jetpack Compose for reusable UI components and seamless user interactions.

- **Control Flow:**
  - Logical navigation paths designed for intuitive user experiences.
  - Modular components ensure scalability and maintainability.

- **Performance Optimization:**
  - Latency tests with Android Profiler to ensure responsive app behavior under real-time data loads.
  - Asynchronous operations with Kotlin Coroutines to handle background tasks efficiently.

## Results

- **Accessibility:**
  - WCAG 2.1 Level AAA compliance with high color contrast and screen reader support.

- **Performance:**
  - Low latency (<200ms) for key operations, ensuring a smooth user experience.

- **Usability:**
  - User-centered design with visual cues, error prevention, and consistent UI components.

## Future Work

- Integrate predictive analytics for resource consumption.
- Enhance energy efficiency for mobile devices.
- Explore advanced security measures to further improve system robustness.

## References

1. R. Guo, “Designing an Android Application for Bills Segregation.” [arXiv preprint arXiv:2104.06823, 2021](https://arxiv.org/abs/2104.06823)
2. K. Akkaya et al., "IoT-based Occupancy Monitoring Techniques for Energy-Efficient Smart Buildings."
3. M.-M. Moazzami et al., "SPOT: A Smartphone-Based Control App with a Device-Agnostic and Adaptive User Interface for IoT Devices."

## Supervisors

- **Mani**
- **Damith Ranasinghe**

## Hardware Provided

- **Android Phone:** Used as a smart camera and interface device.
- **Raspberry Pi Kit:** For base station programming.
- **USB Camera:** Vision-based tracking for activity monitoring.

## Documentation

For more detailed information, check the project documentation: [SplitMate Gamma Project PDF]()


---

SplitMate Gamma redefines the way bills are managed in shared living environments, bringing fairness, transparency, and efficiency to cost-sharing through innovative IoT solutions.
